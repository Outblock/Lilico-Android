package io.outblock.lilico.page.walletcreate.fragments.mnemoniccheck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.getMnemonic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import wallet.core.jni.Mnemonic

class WalletCreateMnemonicCheckViewModel : ViewModel() {

    val mnemonicQuestionLiveData = MutableLiveData<List<MnemonicCheckItem>>()

    fun generateMnemonicQuestion() {
        viewModelIOScope(this) {
            val questionList = mutableListOf<MnemonicCheckItem>()
            val mnemonics = getMnemonic().split(" ").toMutableList()

            var index = listOf(0, 1, 2).shuffled().first()
            var mnemonic = mnemonics[index]
            questionList.add(
                MnemonicCheckItem(
                    index = index,
                    mnemonic = mnemonic,
                    listOf(mnemonics[index], Mnemonic.suggest(mnemonic.take(1)), Mnemonic.suggest(mnemonic.take(1))).shuffled(),
                )
            )

            index = listOf(3, 4, 5).shuffled().shuffled().first()
            mnemonic = mnemonics[index]
            questionList.add(
                MnemonicCheckItem(
                    index = index,
                    mnemonic = mnemonic,
                    listOf(mnemonics[index], Mnemonic.suggest(mnemonic.take(1)), Mnemonic.suggest(mnemonic.take(1))).shuffled(),
                )
            )

            index = listOf(6, 7, 8).shuffled().first()
            mnemonic = mnemonics[index]
            questionList.add(
                MnemonicCheckItem(
                    index = index,
                    mnemonic = mnemonic,
                    listOf(mnemonics[index], Mnemonic.suggest(mnemonic.take(1)), Mnemonic.suggest(mnemonic.take(1))).shuffled(),
                )
            )

            index = listOf(9, 10, 11).shuffled().first()
            mnemonic = mnemonics[index]

            questionList.add(
                MnemonicCheckItem(
                    index = index,
                    mnemonic = mnemonic,
                    listOf(mnemonics[index], Mnemonic.suggest(mnemonic.take(1)), Mnemonic.suggest(mnemonic.take(1))).shuffled(),
                )
            )

            withContext(Dispatchers.Main) {
                mnemonicQuestionLiveData.value = questionList
            }
        }
    }
}