package io.outblock.lilico.page.send.processing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.utils.viewModelIOScope

class SendProcessingViewModel : ViewModel(), OnTransactionStateChange {
    val userInfoLiveData = MutableLiveData<UserInfoData>()

    val amountConvertLiveData = MutableLiveData<Float>()

    val stateChangeLiveData = MutableLiveData<TransactionState>()

    lateinit var state: TransactionState

    fun bindTransactionState(state: TransactionState) {
        this.state = state
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    fun load() {
        viewModelIOScope(this) {
            userInfoCache().read()?.let { userInfo ->
                walletCache().read()?.wallets?.first()?.blockchain?.first()?.address?.let {
                    userInfoLiveData.postValue(userInfo.apply { address = it })
                }
            }
            if (state.type == TransactionState.TYPE_COIN) {
                amountConvertLiveData.postValue(CoinRateManager.usdAmount(amount = state.coinData().amount) {
                    amountConvertLiveData.postValue(it)
                })
            }
        }
    }

    override fun onTransactionStateChange() {
        val state = TransactionStateManager.getLastVisibleTransaction() ?: return
        if (state.transactionId != this.state.transactionId) {
            return
        }
        stateChangeLiveData.postValue(state)
    }

}