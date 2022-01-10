package io.outblock.lilico.wallet

import com.nftco.flow.sdk.DomainTag.normalize
import com.nftco.flow.sdk.bytesToHex
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import wallet.core.jni.CoinType
import wallet.core.jni.Curve
import wallet.core.jni.HDWallet
import wallet.core.jni.Hash

private const val DERIVATION_PATH = "m/44'/539'/0'/0/0"

fun getPublicKey(removePrefix: Boolean = true): String {
    return HDWallet(getMnemonic(), "").getPublicKey(removePrefix)
}

fun HDWallet.getPublicKey(removePrefix: Boolean = true): String {
    val privateKey = getDerivedKey(CoinType.FLOW, 0, 0, 0)
    val publicKey = privateKey.publicKeyNist256p1.uncompressed().data().bytesToHex()

    return if (removePrefix) publicKey.removePrefix("04") else publicKey
}

fun HDWallet.sign(text: String): String {
    val privateKey = getDerivedKey(CoinType.FLOW, 0, 0, 0)
    val data = text.encodeToByteArray()
    val hashedData = Hash.sha256(normalize("FLOW-V0.0-user") + data)
    val signature = privateKey.sign(hashedData, Curve.NIST256P1).dropLast(1).toByteArray()
    return signature.bytesToHex()
}

fun createWalletFromServer() {
    ioScope {
        val service = retrofit().create(ApiService::class.java)
        val resp = service.createWallet()
    }
}

fun String.toAddress(): String {
    if (this.startsWith("0x")) {
        return this
    }
    return "0x$this"
}