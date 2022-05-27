package io.outblock.lilico.wallet

import com.nftco.flow.sdk.DomainTag.normalize
import com.nftco.flow.sdk.bytesToHex
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import wallet.core.jni.Curve
import wallet.core.jni.HDWallet
import wallet.core.jni.Hash

private const val DERIVATION_PATH = "m/44'/539'/0'/0/0"

fun getPublicKey(removePrefix: Boolean = true): String {
    return hdWallet().getPublicKey(removePrefix)
}

fun getPrivateKey(): String {
    return hdWallet().getPrivateKey()
}

fun HDWallet.getPrivateKey(): String {
    return getCurveKey(Curve.SECP256K1, DERIVATION_PATH).data().bytesToHex()
}

fun getWalletCore() = hdWallet()

fun HDWallet.getPublicKey(removePrefix: Boolean = true): String {
    val privateKey = getCurveKey(Curve.SECP256K1, DERIVATION_PATH)
    val publicKey = privateKey.getPublicKeySecp256k1(false).data().bytesToHex()

    return if (removePrefix) publicKey.removePrefix("04") else publicKey
}

fun hdWallet(): HDWallet {
    return HDWallet(getMnemonic(), "")
}

fun HDWallet.sign(text: String, isNeedDomainTag: Boolean = true): String {
    return signData(normalize("FLOW-V0.0-user") + text.encodeToByteArray())
}

fun HDWallet.signData(data: ByteArray): String {
    val privateKey = getCurveKey(Curve.SECP256K1, DERIVATION_PATH)
    val hashedData = Hash.sha256(data)
    val signature = privateKey.sign(hashedData, Curve.SECP256K1).dropLast(1).toByteArray()
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