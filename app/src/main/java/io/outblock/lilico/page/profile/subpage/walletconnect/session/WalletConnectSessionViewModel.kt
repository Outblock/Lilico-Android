package io.outblock.lilico.page.profile.subpage.walletconnect.session

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.R
import io.outblock.lilico.manager.walletconnect.WalletConnect
import io.outblock.lilico.page.profile.subpage.walletconnect.session.model.WalletConnectSessionTitleModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.viewModelIOScope

class WalletConnectSessionViewModel : ViewModel() {

    val dataListLiveData = MutableLiveData<List<Any>>()

    fun load() {
        viewModelIOScope(this) {
            val data = mutableListOf<Any>(WalletConnectSessionTitleModel(R.string.main_wallet.res2String())).apply {
                addAll(WalletConnect.get().sessions())
            }
            if (data.size > 1) {
                dataListLiveData.postValue(data)
            } else {
                dataListLiveData.postValue(emptyList())
            }
        }
    }
}