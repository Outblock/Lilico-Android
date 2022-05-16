package io.outblock.lilico.manager.transaction

import android.os.Parcelable
import androidx.annotation.MainThread
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.FlowId
import com.nftco.flow.sdk.FlowTransactionResult
import com.nftco.flow.sdk.FlowTransactionStatus
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.page.send.nft.NftSendModel
import io.outblock.lilico.page.send.transaction.subpage.amount.model.TransactionModel
import io.outblock.lilico.page.window.bubble.tools.popBubbleStack
import io.outblock.lilico.page.window.bubble.tools.updateBubbleStack
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.safeRun
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import java.lang.ref.WeakReference
import kotlin.math.abs

object TransactionStateManager {
    private val TAG = TransactionStateManager::class.java.simpleName

    private val cache by lazy { CacheManager("transaction_state", TransactionStateData::class.java) }

    private lateinit var stateData: TransactionStateData

    private val onStateChangeCallbacks = mutableListOf<WeakReference<OnTransactionStateChange>>()

    fun init() {
        ioScope {
            stateData = cache.read() ?: TransactionStateData(mutableListOf())
            loopState()
        }
    }

    fun getTransactionStateList() = stateData.data.toList()

    fun addOnTransactionStateChange(callback: OnTransactionStateChange) {
        onStateChangeCallbacks.add(WeakReference(callback))
    }

    @MainThread
    fun newTransaction(transactionState: TransactionState) {
        stateData.data.add(transactionState)
        updateState(transactionState)
        loopState()
    }

    fun getLastVisibleTransaction(): TransactionState? {
        return stateData.data.toList().firstOrNull {
            (it.state < FlowTransactionStatus.SEALED.num && it.state > FlowTransactionStatus.UNKNOWN.num)
              || (it.state == FlowTransactionStatus.SEALED.num && abs(it.updateTime - System.currentTimeMillis()) < 5000)
        }
    }

    fun getTransactionStateById(transactionId: String): TransactionState? {
        return stateData.data.toList().firstOrNull { it.transactionId == transactionId }
    }

    private fun loopState() {
        ioScope {
            var ret: FlowTransactionResult
            while (true) {
                val stateQueue = stateData.unsealedState()

                if (stateQueue.isEmpty()) {
                    break
                }

                safeRun {
                    for (state in stateQueue) {
                        ret = checkNotNull(
                            FlowApi.get().getTransactionResultById(FlowId.of(state.transactionId.hexToBytes()))
                        ) { "Transaction with that id not found" }
                        if (ret.status.num != state.state) {
                            state.state = ret.status.num
                            logd(TAG, "update state:${ret.status}")
                            updateState(state)
                        }
                    }
                }

                delay(500)
            }
        }
    }

    private fun updateState(state: TransactionState) {
        state.updateTime = System.currentTimeMillis()
        ioScope { cache.cache(stateData) }
        logd(TAG, "updateState:$state")
        dispatchCallback()
        updateBubbleStack(state)
        if (state.isUnknown() || state.isSealed()) {
            uiScope {
                delay(3000)
                popBubbleStack(state)
            }
        }
        if (state.type == TransactionState.TYPE_ADD_TOKEN && state.isSuccess()) {
            TokenStateManager.fetchStateSingle(state.tokenData(), cache = true)
        }
    }

    private fun dispatchCallback() {
        uiScope {
            onStateChangeCallbacks.removeAll { it.get() == null }
            onStateChangeCallbacks.forEach { it.get()?.onTransactionStateChange() }
        }
    }

    private fun TransactionStateData.unsealedState(): List<TransactionState> {
        return data.toList().filter { it.state.isProcessing() }
    }

    private fun Int.isProcessing() = this < FlowTransactionStatus.SEALED.num && this > FlowTransactionStatus.UNKNOWN.num

    private fun Int.isUnknown() = this == FlowTransactionStatus.UNKNOWN.num || this == FlowTransactionStatus.EXPIRED.num
}

interface OnTransactionStateChange {
    fun onTransactionStateChange()
}


class TransactionStateData(
    @SerializedName("data")
    val data: MutableList<TransactionState>,
)

@Parcelize
data class TransactionState(
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("time")
    val time: Long,
    @SerializedName("updateTime")
    var updateTime: Long = 0,

    // @FlowTransactionStatus
    @SerializedName("state")
    var state: Int,
    @SerializedName("type")
    val type: Int,

    /**
     * TYPE_COIN = TransactionModel
     * TYPE_NFT = NftSendModel
     */
    @SerializedName("data")
    val data: String,
) : Parcelable {
    companion object {
        const val TYPE_NFT = 1
        const val TYPE_TRANSFER_COIN = 2
        const val TYPE_ADD_TOKEN = 3
        const val TYPE_ENABLE_NFT = 4
        const val TYPE_TRANSFER_NFT = 5
    }

    fun coinData() = Gson().fromJson(data, TransactionModel::class.java)

    fun nftData() = Gson().fromJson(data, NftSendModel::class.java)

    fun tokenData() = Gson().fromJson(data, FlowCoin::class.java)

    fun nftCollectionData() = Gson().fromJson(data, NftCollection::class.java)

    fun nftSendData() = Gson().fromJson(data, NftSendModel::class.java)

    fun contact() = if (type == TYPE_TRANSFER_COIN) coinData().target else nftData().target

    fun isSuccess() = state == FlowTransactionStatus.SEALED.num

    fun isFailed() = state == FlowTransactionStatus.UNKNOWN.num || state == FlowTransactionStatus.EXPIRED.num

    fun isProcessing() = state > FlowTransactionStatus.UNKNOWN.num && state < FlowTransactionStatus.SEALED.num

    fun isUnknown() = state == FlowTransactionStatus.UNKNOWN.num || state == FlowTransactionStatus.EXPIRED.num

    fun isSealed() = state == FlowTransactionStatus.SEALED.num

    fun progress(): Float {
        return when (state) {
            FlowTransactionStatus.PENDING.num -> 0.25f
            FlowTransactionStatus.FINALIZED.num -> 0.50f
            FlowTransactionStatus.EXECUTED.num -> 0.75f
            FlowTransactionStatus.SEALED.num -> 1.0f
            else -> 0.0f
        }
    }
}