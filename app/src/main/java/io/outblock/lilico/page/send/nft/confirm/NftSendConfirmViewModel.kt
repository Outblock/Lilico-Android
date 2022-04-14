package io.outblock.lilico.page.send.nft.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.cadenceTransferNft
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.bubble.sendstate.SendStateBubble
import io.outblock.lilico.page.send.nft.NftSendModel
import io.outblock.lilico.utils.viewModelIOScope

class NftSendConfirmViewModel : ViewModel() {

    val userInfoLiveData = MutableLiveData<UserInfoData>()

    val resultLiveData = MutableLiveData<Boolean>()

    lateinit var nft: NftSendModel

    fun bindSendModel(nft: NftSendModel) {
        this.nft = nft
    }

    fun load() {
        viewModelIOScope(this) {
            userInfoCache().read()?.let { userInfo ->
                walletCache().read()?.wallets?.first()?.blockchain?.first()?.address?.let {
                    userInfoLiveData.postValue(userInfo.apply { address = it })
                }
            }
        }
    }

    fun send() {
        viewModelIOScope(this) {
            val tid = cadenceTransferNft(nft.target.address!!, nft.nft)
            resultLiveData.postValue(tid != null)
            if (tid.isNullOrBlank()) {
                return@viewModelIOScope
            }
            TransactionStateManager.newTransaction(
                TransactionState(
                    transactionId = tid,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_TRANSFER_NFT,
                    data = Gson().toJson(nft),
                )
            )
            BaseActivity.getCurrentActivity()?.let { SendStateBubble.show(it) }
        }
    }
}