package io.outblock.lilico.wallet

import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import wallet.core.jni.HDWallet
import java.util.*

fun getMnemonic(): String {
    var str = aesDecrypt(getMnemonicAesKey(), message = getMnemonicFromPreference())

    if (str.isBlank()) {
        str = HDWallet(128, "").mnemonic()
        saveMnemonic(aesEncrypt(getMnemonicAesKey(), message = str))
    }
    return str
}

fun updateMnemonicAesKey(key: String) {
    ioScope {
        val mnemonic = getMnemonic()
        saveMnemonic(aesEncrypt(key, message = mnemonic))
        updateAesLocalCode(key)
    }
}

fun getMnemonicAesKey(): String {
    var key = getAesLocalCode()
    if (key.isEmpty()) {
        key = UUID.randomUUID().toString().take(16)
        updateAesLocalCode(key)
    }
    return key
}