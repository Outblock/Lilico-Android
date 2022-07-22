package io.outblock.lilico.wallet

import com.nftco.flow.sdk.DomainTag.normalize
import com.nftco.flow.sdk.bytesToHex
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
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
    return Wallet.store().wallet()
}

fun HDWallet.sign(text: String, domainTag: ByteArray = normalize("FLOW-V0.0-user")): String {
    return signData(domainTag + text.encodeToByteArray())
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
        logd("createWalletFromServer", "$resp")
    }
}

fun String.toAddress(): String = "0x" + removeAddressPrefix()

fun String.removeAddressPrefix(): String = this.removePrefix("0x").removePrefix("Fx")