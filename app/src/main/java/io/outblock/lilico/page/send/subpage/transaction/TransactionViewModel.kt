package io.outblock.lilico.page.send.subpage.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.flowjvm.FlowJvmTransaction
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.send.subpage.amount.model.TransactionModel
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
            val result =
                FlowJvmTransaction().send(transaction.fromAddress.toAddress(), transaction.target.address.orEmpty().toAddress(), transaction.amount)
            resultLiveData.postValue(result != null && result.errorMessage.isBlank())
        }
    }
}