package io.outblock.lilico.page.walletcreate.fragments.mnemonic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.utils.getMnemonic
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.saveMnemonic
import io.outblock.lilico.utils.viewModelIOScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import wallet.core.jni.HDWallet

class CreateMnemonicViewModel : ViewModel() {

    val mnemonicList = MutableLiveData<List<MnemonicModel>>()

    fun loadMnemonic() {
        viewModelIOScope(this) {
            var str = getMnemonic()
            if (str.isBlank()) {
                str = HDWallet(128, "").mnemonic()
                saveMnemonic(str)
            }
            withContext(Dispatchers.Main) {
                mnemonicList.value = str.split(" ").mapIndexed { index, s -> MnemonicModel(index + 1, s) }
            }

            logd("Mnemonic", str)
        }
    }
}