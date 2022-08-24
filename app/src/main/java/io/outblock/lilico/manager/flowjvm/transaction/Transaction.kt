package io.outblock.lilico.manager.flowjvm.transaction

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.manager.config.AppConfig
import io.outblock.lilico.manager.config.isGasFree
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.flowjvm.replaceFlowAddress
import io.outblock.lilico.network.functions.FUNCTION_SIGN_AS_PAYER
import io.outblock.lilico.network.functions.executeHttpFunction
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.vibrateTransaction
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.wallet.toAddress
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

private const val TAG = "FlowTransaction"

suspend fun sendTransaction(
    builder: TransactionBuilder.() -> Unit,
): String {
    updateSecurityProvider()

    logd(TAG, "sendTransaction prepare")
    val voucher = prepare(TransactionBuilder().apply { builder(this) })

    logd(TAG, "sendTransaction build flow transaction")
    var tx = voucher.toFlowTransaction()

    if (tx.envelopeSignatures.isEmpty() && isGasFree()) {
        logd(TAG, "sendTransaction request free gas envelope")
        tx = tx.addFreeGasEnvelope()
    } else if (tx.envelopeSignatures.isEmpty()) {
        logd(TAG, "sendTransaction sign envelope")
        tx = tx.addLocalSignatures()
    }

    logd(TAG, "sendTransaction to flow chain")
    val txID = FlowApi.get().sendTransaction(tx)
    logd(TAG, "transaction id:$${txID.bytes.bytesToHex()}")
    vibrateTransaction()
    return txID.bytes.bytesToHex()
}

private fun FlowTransaction.addLocalSignatures(): FlowTransaction {
    return copy(payloadSignatures = emptyList()).addEnvelopeSignature(
        payerAddress,
        keyIndex = 0,
        Crypto.getSigner(
            privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
            hashAlgo = HashAlgorithm.SHA2_256
        )
    )
}

private suspend fun FlowTransaction.addFreeGasEnvelope(): FlowTransaction {
    val response = executeHttpFunction(FUNCTION_SIGN_AS_PAYER, buildPayerSignable())
    logd(TAG, "response:$response")

    val sign = Gson().fromJson(response, SignPayerResponse::class.java).envelopeSigs

    return addEnvelopeSignature(
        FlowAddress(sign.address),
        keyIndex = sign.keyId,
        signature = FlowSignature(sign.sig)
    )
}

private suspend fun prepare(builder: TransactionBuilder): Voucher {
    logd(TAG, "prepare builder:$builder")
    val account = FlowApi.get().getAccountAtLatestBlock(FlowAddress(builder.walletAddress?.toAddress().orEmpty()))
        ?: throw RuntimeException("get wallet account error")
    return Voucher(
        arguments = builder.arguments.map { AsArgument(it.type, it.value?.toString().orEmpty()) },
        cadence = builder.script?.replaceFlowAddress(),
        computeLimit = builder.limit ?: 9999,
        payer = builder.payer ?: (if (isGasFree()) AppConfig.payer().address else builder.walletAddress),
        proposalKey = ProposalKey(
            address = account.address.base16Value,
            keyId = account.keys.first().id,
            sequenceNum = account.keys.first().sequenceNumber,
        ),
        refBlock = FlowApi.get().getLatestBlockHeader().id.base16Value,
    )
}

fun FlowTransaction.buildPayerSignable(): PayerSignable? {
    val payerAccount = FlowApi.get().getAccountAtLatestBlock(payerAddress) ?: return null
    val voucher = Voucher(
        cadence = script.stringValue,
        refBlock = referenceBlockId.base16Value,
        computeLimit = gasLimit.toInt(),
        arguments = arguments.map { it.jsonCadence }.map { AsArgument(it.type, it.value.toString()) },
        proposalKey = ProposalKey(
            address = proposalKey.address.base16Value.toAddress(),
            keyId = proposalKey.keyIndex,
            sequenceNum = proposalKey.sequenceNumber.toInt(),
        ),
        payer = payerAddress.base16Value.toAddress(),
        authorizers = authorizers.map { it.base16Value.toAddress() },
        payloadSigs = payloadSignatures.map {
            Singature(
                address = it.address.base16Value.toAddress(),
                keyId = it.keyIndex,
                sig = it.signature.base16Value,
            )
        },
        envelopeSigs = listOf(
            Singature(
                address = AppConfig.payer().address.toAddress(),
                keyId = payerAccount.keys.first().id,
            )
        ),
    )

    return PayerSignable(
        transaction = voucher,
        message = PayerSignable.Message(
            (DomainTag.TRANSACTION_DOMAIN_TAG + canonicalAuthorizationEnvelope).bytesToHex()
        )
    )
}

fun FlowTransaction.encodeTransactionPayload(): String {
    return (DomainTag.TRANSACTION_DOMAIN_TAG + canonicalPayload).bytesToHex()
}

fun Voucher.toFlowTransaction(): FlowTransaction {
    val transaction = this
    var tx = flowTransaction {
        script { transaction.cadence.orEmpty() }

        arguments = transaction.arguments.orEmpty().map {
            val jsonObject = JsonObject()
            jsonObject.addProperty("type", it.type)
            jsonObject.addProperty("value", it.value.toString())
            jsonObject.toString().toByteArray()
        }.map { FlowArgument(it) }.toMutableList()

        referenceBlockId = FlowId(transaction.refBlock.orEmpty())

        gasLimit = computeLimit ?: 9999

        proposalKey {
            address = FlowAddress(transaction.proposalKey.address.orEmpty())
            keyIndex = transaction.proposalKey.keyId ?: 0
            sequenceNumber = transaction.proposalKey.sequenceNum ?: 0
        }

        if (transaction.authorizers.isNullOrEmpty()) {
            authorizers(mutableListOf(FlowAddress(transaction.proposalKey.address.orEmpty())))
        } else {
            authorizers(transaction.authorizers.map { FlowAddress(it) }.toMutableList())
        }

        payerAddress = FlowAddress(transaction.payer.orEmpty())

        addPayloadSignatures {
            payloadSigs?.forEach { sig ->
                if (!sig.sig.isNullOrBlank()) {
                    signature(
                        FlowAddress(sig.address),
                        sig.keyId ?: 0,
                        FlowSignature(sig.sig.orEmpty())
                    )
                }
            }
        }

        addEnvelopeSignatures {
            envelopeSigs?.forEach { sig ->
                if (!sig.sig.isNullOrBlank()) {
                    signature(
                        FlowAddress(sig.address),
                        sig.keyId ?: 0,
                        FlowSignature(sig.sig.orEmpty())
                    )
                }
            }
        }
    }

    if (tx.payloadSignatures.isEmpty()) {
        tx = tx.addPayloadSignature(
            FlowAddress(proposalKey.address.orEmpty()),
            keyIndex = proposalKey.keyId ?: 0,
            Crypto.getSigner(
                privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                hashAlgo = HashAlgorithm.SHA2_256
            ),
        )
    }

    return tx
}

/**
 * fix: java.security.NoSuchAlgorithmException: no such algorithm: ECDSA for provider BC
 */
fun updateSecurityProvider() {
    // Web3j will set up the provider lazily when it's first used.
    val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) ?: return
    if (provider.javaClass == BouncyCastleProvider::class.java) {
        // BC with same package name, shouldn't happen in real life.
        return
    }
    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
    Security.insertProviderAt(BouncyCastleProvider(), 1)
}