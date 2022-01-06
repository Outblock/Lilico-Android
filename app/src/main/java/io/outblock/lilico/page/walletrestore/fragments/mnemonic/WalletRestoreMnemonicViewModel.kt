package io.outblock.lilico.page.walletrestore.fragments.mnemonic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wallet.core.jni.Mnemonic

class WalletRestoreMnemonicViewModel : ViewModel() {

    val mnemonicSuggestListLiveData = MutableLiveData<List<String>>()

    val selectSuggestLiveData = MutableLiveData<String>()

    val invalidWordListLiveData = MutableLiveData<List<Pair<Int, String>>>()

    fun suggestMnemonic(text: String) {
        val prefix = text.split(" ").last()
        val list = Mnemonic.suggest(prefix).split(" ")
        mnemonicSuggestListLiveData.postValue(list)
    }

    fun invalidMnemonicCheck(text: String) {
    }

    fun selectSuggest(word: String) {
        selectSuggestLiveData.postValue(word)
    }
}