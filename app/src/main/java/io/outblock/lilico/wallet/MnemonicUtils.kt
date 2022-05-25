package io.outblock.lilico.wallet

import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import wallet.core.jni.HDWallet
import java.util.*

fun getMnemonic(): String {
    var str = aesDecrypt(mnemonicAesKey(), message = getMnemonicFromPreference())

    if (str.isBlank()) {
        str = HDWallet(128, "").mnemonic()
        saveMnemonic(aesEncrypt(mnemonicAesKey(), message = str))
    }
    return str
}

fun updatePinCode(pinCode: String) {
    ioScope {
        val mnemonic = getMnemonic()
        savePinCode(pinCode)
        saveMnemonic(aesEncrypt(mnemonicAesKey(), message = mnemonic))
    }
}

fun mnemonicAesKey(): String {
    return getPinCode() + randomCode()
}

private fun randomCode(): String {
    var key = getAesLocalCode()
    if (key.isEmpty()) {
        key = UUID.randomUUID().toString().take(16)
        updateAesLocalCode(key)
    }
    return key
}