package io.outblock.lilico.page.profile.subpage.developer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.viewModelIOScope

class DeveloperModeViewModel : ViewModel() {
    val progressVisibleLiveData = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Boolean>()

    fun changeNetwork() {
        viewModelIOScope(this) {
            FlowApi.refreshConfig()
            val cacheExist = WalletManager.wallet() != null && !WalletManager.wallet()?.walletAddress().isNullOrBlank()
            if (!cacheExist) {
                progressVisibleLiveData.postValue(true)
                try {
                    val service = retrofit().create(ApiService::class.java)
                    val resp = service.getWalletList()

                    // request success & wallet list is empty (wallet not create finish)
                    if (!resp.data!!.wallets.isNullOrEmpty()) {
                        WalletManager.update(resp.data)
                        resultLiveData.postValue(true)
                    }
                } catch (e: Exception) {
                    loge(e)
                    resultLiveData.postValue(false)
                }
                progressVisibleLiveData.postValue(false)
            }
        }
    }
}