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
            val str = getMnemonic()
            withContext(Dispatchers.Main) {
                val list = str.split(" ").mapIndexed { index, s -> MnemonicModel(index + 1, s) }
                val result = mutableListOf<MnemonicModel>()
                (0 until list.size / 2).forEach{  i ->
                    result.add(list[i])
                    result.add(list[i + list.size / 2])
                }
                mnemonicList.value = result
            }

            logd("Mnemonic", str)
        }
    }
}