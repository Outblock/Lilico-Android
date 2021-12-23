package io.outblock.lilico.wallet

import io.outblock.lilico.utils.getAesLocalCode
import io.outblock.lilico.utils.getMnemonicFromPreference
import io.outblock.lilico.utils.saveMnemonic
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import io.outblock.lilico.utils.updateAesLocalCode
import wallet.core.jni.HDWallet
import java.util.*

fun getMnemonic(): String {
    var str = aesDecrypt(getAesKey(), message = getMnemonicFromPreference())

    if (str.isBlank()) {
        str = HDWallet(128, "").mnemonic()
        saveMnemonic(aesEncrypt(getAesKey(), message = str))
    }
    return str
}

private fun getAesKey(): String {
    var key = getAesLocalCode()
    if (key.isEmpty()) {
        key = UUID.randomUUID().toString().take(16)
        updateAesLocalCode(key)
    }
    return key
}