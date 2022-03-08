package io.outblock.lilico.page.send.nft.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.model.UserInfoData
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
        }
    }
}