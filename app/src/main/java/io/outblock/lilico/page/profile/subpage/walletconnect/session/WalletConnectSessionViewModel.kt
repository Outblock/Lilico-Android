package io.outblock.lilico.page.profile.subpage.walletconnect.session

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.R
import io.outblock.lilico.manager.walletconnect.WalletConnect
import io.outblock.lilico.manager.walletconnect.getWalletConnectPendingRequests
import io.outblock.lilico.page.profile.subpage.walletconnect.session.model.PendingRequestModel
import io.outblock.lilico.page.profile.subpage.walletconnect.session.model.WalletConnectSessionTitleModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.viewModelIOScope

class WalletConnectSessionViewModel : ViewModel() {

    val dataListLiveData = MutableLiveData<List<Any>>()

    fun load() {
        viewModelIOScope(this) {
            val data = mutableListOf<Any>().apply {
                val sessions = WalletConnect.get().sessions()
                val requests = getWalletConnectPendingRequests().map { request ->
                    PendingRequestModel(
                        request = request,
                        metadata = sessions.firstOrNull { request.topic == it.topic }?.metaData
                    )
                }.filter { it.metadata != null }
                if (requests.isNotEmpty()) {
                    add(WalletConnectSessionTitleModel(R.string.pending_request.res2String()))
                    addAll(requests)
                }

                add(WalletConnectSessionTitleModel(R.string.connected_site.res2String()))
                addAll(sessions)
            }
            if (data.size > 1) {
                dataListLiveData.postValue(data)
            } else {
                dataListLiveData.postValue(emptyList())
            }
        }
    }
}