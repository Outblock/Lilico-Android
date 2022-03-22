package io.outblock.lilico.page.send.transaction.subpage.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.flowjvm.cadenceTransferToken
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.bubble.sendstate.SendStateBubble
import io.outblock.lilico.page.send.transaction.subpage.amount.model.TransactionModel
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.toAddress

class TransactionViewModel : ViewModel() {

    val userInfoLiveData = MutableLiveData<UserInfoData>()

    val amountConvertLiveData = MutableLiveData<Float>()

    val resultLiveData = MutableLiveData<Boolean>()

    lateinit var transaction: TransactionModel

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
            amountConvertLiveData.postValue(CoinRateManager.usdAmount(amount = transaction.amount) {
                amountConvertLiveData.postValue(it)
            })
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
}