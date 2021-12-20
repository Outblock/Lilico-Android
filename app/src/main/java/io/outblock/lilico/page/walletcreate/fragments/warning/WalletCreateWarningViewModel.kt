package io.outblock.lilico.page.walletcreate.fragments.warning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.registerOutblockUser
import io.outblock.lilico.utils.getUsername
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.viewModelIOScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletCreateWarningViewModel : ViewModel() {

    val registerCallbackLiveData = MutableLiveData<Boolean>()

    fun register() {
        viewModelIOScope(this) {
            registerOutblockUser(getUsername()) {
                ioScope { withContext(Dispatchers.Main) { registerCallbackLiveData.postValue(it) } }
            }
        }
    }
}