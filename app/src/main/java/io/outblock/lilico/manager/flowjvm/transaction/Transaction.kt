package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logv
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.wallet.toAddress
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

private const val TAG = "FlowTransaction"

fun sendFlowTransaction(
    script: String,
    fromAddress: String,
    arguments: FlowArgumentsBuilder.() -> Unit,
): String? {
    logv(TAG, "transaction script:$script")
    updateSecurityProvider()

    return try {
        if (GasConfig.isFreeGas()) {
            sendTransactionFreeGas(script, fromAddress, arguments)
        } else sendTransactionNormal(script, fromAddress, arguments)
    } catch (e: Exception) {
        loge(TAG, e)
        null
    }
}

private fun sendTransactionFreeGas(
    script: String,
    fromAddress: String,
    arguments: FlowArgumentsBuilder.() -> Unit,
): String {
    val latestBlockId = FlowApi.get().getLatestBlockHeader().id
}

private fun sendTransactionNormal(
    script: String,
    fromAddress: String,
    arguments: FlowArgumentsBuilder.() -> Unit,
): String {
    val latestBlockId = FlowApi.get().getLatestBlockHeader().id

    val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress.toAddress()))!!

    val tx = flowTransaction {
        script { script.replaceFlowAddress() }

        arguments { arguments() }

        referenceBlockId = latestBlockId
        gasLimit = 100

        proposalKey {
            address = payerAccount.address
            keyIndex = payerAccount.keys[0].id
            sequenceNumber = payerAccount.keys[0].sequenceNumber.toLong()
        }

        authorizers(mutableListOf(FlowAddress(fromAddress.toAddress())))
        payerAddress = payerAccount.address

        signatures {
            signature {
                address = payerAccount.address
                keyIndex = 0
                signer = Crypto.getSigner(
                    privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                    hashAlgo = HashAlgorithm.SHA2_256
                )
            }
        }
    }

    val txID = FlowApi.get().sendTransaction(tx)
    logd(TAG, "transaction id:$${txID.bytes.bytesToHex()}")
    return txID.bytes.bytesToHex()
}

/**
 * fix: java.security.NoSuchAlgorithmException: no such algorithm: ECDSA for provider BC
 */
private fun updateSecurityProvider() {
    // Web3j will set up the provider lazily when it's first used.
    val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) ?: return
    if (provider.javaClass == BouncyCastleProvider::class.java) {
        // BC with same package name, shouldn't happen in real life.
        return
    }
    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
    Security.insertProviderAt(BouncyCastleProvider(), 1)
}