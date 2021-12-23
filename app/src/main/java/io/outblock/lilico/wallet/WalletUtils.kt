package io.outblock.lilico.wallet

import com.nftco.flow.sdk.bytesToHex
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet


fun getPublicKey(removePrefix: Boolean = true): String {
    val wallet = HDWallet(getMnemonic(), "")
    val privateKey = wallet.getDerivedKey(CoinType.FLOW, 0, 0, 0)
    val publicKey = privateKey.publicKeyNist256p1.uncompressed().data().bytesToHex()

    return if (removePrefix) publicKey.removePrefix("04") else publicKey
}

fun createWalletFromServer() {
    ioScope {
        val service = retrofit().create(ApiService::class.java)
        val resp = service.createWallet()
    }
}