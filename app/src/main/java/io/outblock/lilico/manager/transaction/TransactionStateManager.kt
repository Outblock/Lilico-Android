package io.outblock.lilico.manager.transaction

import androidx.annotation.MainThread
import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.FlowId
import com.nftco.flow.sdk.FlowTransactionResult
import com.nftco.flow.sdk.FlowTransactionStatus
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.safeRun
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay
import java.lang.ref.WeakReference
import kotlin.math.abs

object TransactionStateManager {
    private val cache by lazy { CacheManager("transaction_state", TransactionStateData::class.java) }

    private lateinit var stateData: TransactionStateData

    private val onStateChangeCallbacks = mutableListOf<WeakReference<OnTransactionStateChange>>()

    fun init() {
        ioScope { stateData = cache.read() ?: TransactionStateData(mutableListOf()) }
    }

    fun addOnTransactionStateChange(callback: OnTransactionStateChange) {
        onStateChangeCallbacks.add(WeakReference(callback))
    }

    @MainThread
    fun newTransaction(transactionState: TransactionState) {
        stateData.data.add(transactionState)
        ioScope { cache.cache(stateData) }
        loopState()
    }

    fun getLastVisibleTransaction(): TransactionState? {
        return stateData.data.firstOrNull {
            it.state < FlowTransactionStatus.SEALED.num
              || (it.state == FlowTransactionStatus.SEALED.num && abs(it.updateTime - System.currentTimeMillis()) < 5000)
        }
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
        dispatchCallback()
    }

    private fun dispatchCallback() {
        uiScope {
            onStateChangeCallbacks.removeAll { it.get() == null }
            onStateChangeCallbacks.forEach { it.get()?.onTransactionStateChange() }
        }
    }

    private fun TransactionStateData.unsealedState(): List<TransactionState> {
        return data.filter { it.state.isProcessing() }
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

class TransactionState(
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
    @SerializedName("data")
    val data: String,
) {
    companion object {
        const val TYPE_NFT = 1
        const val TYPE_COIN = 2
    }
}