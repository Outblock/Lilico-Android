package io.outblock.lilico.widgets.webview.fcl

import com.nftco.flow.sdk.DomainTag
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.wallet.removeAddressPrefix
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthnResponse
import org.tdf.rlp.RLP
import org.tdf.rlp.RLPCodec

private val accountProofTag = DomainTag.normalize("FCL-ACCOUNT-PROOF-V0.0")

// encode flow jvm account proof
fun FclAuthnResponse.encodeAccountProof(address: String, includeDomainTag: Boolean = true): ByteArray {
    val nonce = body.nonce ?: return byteArrayOf()
    val appIdentifier = body.appIdentifier ?: throw IllegalStateException("Encode Message For Provable Authn Error: appIdentifier must be defined")
    return encodeAccountProof(address, nonce, appIdentifier, includeDomainTag)
}

fun encodeAccountProof(
    address: String,
    nonce: String,
    appIdentifier: String,
    includeDomainTag: Boolean,
): ByteArray {
    assert(address.isNotBlank()) { "Encode Message For Provable Authn Error: address must be defined" }
    assert(nonce.length >= 64) { "Encode Message For Provable Authn Error: nonce must be minimum of 32 bytes" }

    val rpl = RLPCodec.encode(AccountProof(appIdentifier, address.removeAddressPrefix().hexToBytes(), nonce.hexToBytes()))

    return if (includeDomainTag) {
        accountProofTag + rpl
    } else rpl
}

private class AccountProof(
    @RLP(0) val appIdentifier: String,
    @RLP(1) val address: ByteArray,
    @RLP(2) val nonce: ByteArray,
)