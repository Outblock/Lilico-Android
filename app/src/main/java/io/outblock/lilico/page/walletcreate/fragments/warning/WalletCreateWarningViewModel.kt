package io.outblock.lilico.page.walletcreate.fragments.warning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.firebase.messaging.uploadPushToken
import io.outblock.lilico.network.registerOutblockUser
import io.outblock.lilico.utils.getUsername
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.Wallet
import io.outblock.lilico.wallet.createWalletFromServer
import java.util.*

class WalletCreateWarningViewModel : ViewModel() {

    val registerCallbackLiveData = MutableLiveData<Boolean>()

    fun register() {
        viewModelIOScope(this) {
            try {
                registerOutblockUser(getUsername().lowercase(Locale.getDefault())) { isSuccess ->
                    uiScope { registerCallbackLiveData.postValue(isSuccess) }
                    if (isSuccess) {
                        uploadPushToken()
                        createWalletFromServer()
                        Wallet.store().store()
                    }
                }
            } catch (e: Exception) {
                uiScope { registerCallbackLiveData.postValue(false) }
            }
        }
    }
}