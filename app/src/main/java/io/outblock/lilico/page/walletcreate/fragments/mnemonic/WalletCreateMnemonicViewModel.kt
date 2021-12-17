package io.outblock.lilico.page.walletcreate.fragments.mnemonic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.getMnemonic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletCreateMnemonicViewModel : ViewModel() {

    val mnemonicList = MutableLiveData<List<MnemonicModel>>()

    fun loadMnemonic() {
        viewModelIOScope(this) {
            var str = getMnemonic()
            withContext(Dispatchers.Main) {
                mnemonicList.value = str.split(" ").mapIndexed { index, s -> MnemonicModel(index + 1, s) }
            }

            logd("Mnemonic", str)
        }
    }
}