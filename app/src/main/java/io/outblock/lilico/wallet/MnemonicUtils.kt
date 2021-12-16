package io.outblock.lilico.wallet

import io.outblock.lilico.utils.getMnemonicFromPreference
import io.outblock.lilico.utils.saveMnemonic
import wallet.core.jni.HDWallet

fun getMnemonic(): String {
    var str = getMnemonicFromPreference()
    if (str.isBlank()) {
        str = HDWallet(128, "").mnemonic()
        saveMnemonic(str)
    }
    return str
}