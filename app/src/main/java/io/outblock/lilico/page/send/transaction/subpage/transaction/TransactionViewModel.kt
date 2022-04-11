package io.outblock.lilico.page.send.transaction.subpage.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.manager.flowjvm.cadenceTransferToken
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.model.CoinRate
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.bubble.sendstate.SendStateBubble
import io.outblock.lilico.page.send.transaction.subpage.amount.model.TransactionModel
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.toAddress

class TransactionViewModel : ViewModel(), OnCoinRateUpdate {

    val userInfoLiveData = MutableLiveData<UserInfoData>()

    val amountConvertLiveData = MutableLiveData<Float>()

    val resultLiveData = MutableLiveData<Boolean>()

    lateinit var transaction: TransactionModel

    init {
        CoinRateManager.addListener(this)
    }

    fun bindTransaction(transaction: TransactionModel) {
        this.transaction = transaction
    }

    fun load() {
        viewModelIOScope(this) {
            userInfoCache().read()?.let { userInfo ->
                walletCache().read()?.wallets?.first()?.blockchain?.first()?.address?.let {
                    userInfoLiveData.postValue(userInfo.apply { address = it })
                }
            }

            val flow = FlowCoinListManager.coinList().first { it.symbol == "flow" }
            CoinRateManager.fetchCoinRate(flow)
        }
    }

    fun send() {
        viewModelIOScope(this) {
            val tid = cadenceTransferToken(transaction.fromAddress.toAddress(), transaction.target.address.orEmpty().toAddress(), transaction.amount)
            resultLiveData.postValue(tid != null)
            if (tid.isNullOrBlank()) {
                return@viewModelIOScope
            }
            TransactionStateManager.newTransaction(
                TransactionState(
                    transactionId = tid,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_COIN,
                    data = Gson().toJson(transaction),
                )
            )
            BaseActivity.getCurrentActivity()?.let { SendStateBubble.show(it) }
        }
    }

    override fun onCoinRateUpdate(coin: FlowCoin, rate: CoinRate) {
        if (coin.symbol != transaction.coinSymbol) {
            return
        }
        amountConvertLiveData.postValue((rate.usdRate()?.price ?: 0.0f) * transaction.amount)
    }
}