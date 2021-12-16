package io.outblock.lilico.wallet

import com.nftco.flow.sdk.bytesToHex
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet


fun getPublicKey(removePrefix: Boolean = true): String {
    val wallet = HDWallet(getMnemonic(), "")
    val privateKey = wallet.getDerivedKey(CoinType.FLOW, 0, 0, 0)
    val publicKey = privateKey.publicKeyNist256p1.uncompressed().data().bytesToHex()

    return if (removePrefix) publicKey.removePrefix("04") else publicKey
}