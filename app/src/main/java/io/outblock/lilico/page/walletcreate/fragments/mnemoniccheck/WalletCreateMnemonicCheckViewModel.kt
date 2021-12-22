package io.outblock.lilico.page.walletcreate.fragments.mnemoniccheck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.page.walletcreate.fragments.mnemonic.MnemonicModel

class WalletCreateMnemonicCheckViewModel : ViewModel() {

    val mnemonicList = MutableLiveData<List<MnemonicModel>>()
}