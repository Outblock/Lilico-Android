package io.outblock.lilico.manager.flowjvm.transaction

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.flowjvm.CadenceArgumentsBuilder
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.flowjvm.builder
import io.outblock.lilico.manager.flowjvm.replaceFlowAddress
import io.outblock.lilico.network.functions.FUNCTION_SIGN_AS_PAYER
import io.outblock.lilico.network.functions.executeFunction
import io.outblock.lilico.utils.logd
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.wallet.toAddress
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

private const val TAG = "FlowTransaction"

suspend fun sendTransaction(
    builder: TransactionBuilder.() -> Unit,
): String? {
    updateSecurityProvider()

    val voucher = prepare(TransactionBuilder().apply { builder(this) })

    val tx = voucher.toFlowTransaction()

    if (tx.envelopeSignatures.isEmpty() && GasConfig.isFreeGas()) {
        tx.addFreeGasEnvelope()
    }

    val txID = FlowApi.get().sendTransaction(tx)
    logd(TAG, "transaction id:$${txID.bytes.bytesToHex()}")
    return txID.bytes.bytesToHex()
}

private suspend fun FlowTransaction.addFreeGasEnvelope(): FlowTransaction {
    val response = executeFunction(FUNCTION_SIGN_AS_PAYER, buildPayerSignable())
    logd(TAG, "response:$response")

    val sign = Gson().fromJson(response, SignPayerResponse::class.java).envelopeSigs

    return addEnvelopeSignature(
        FlowAddress(sign.address),
        keyIndex = sign.keyId,
        signature = FlowSignature(sign.sig)
    )
}

private fun prepare(builder: TransactionBuilder): Voucher {
    val account = FlowApi.get().getAccountAtLatestBlock(FlowAddress(builder.walletAddress?.toAddress().orEmpty()))
        ?: throw RuntimeException("get wallet account error")
    return Voucher(
        arguments = builder.arguments.map { AsArgument(it.type, it.value?.toString().orEmpty()) },
        cadence = builder.script?.replaceFlowAddress(),
        computeLimit = builder.limit ?: 9999,
        payer = builder.payer ?: (if (GasConfig.isFreeGas()) GasConfig.payer().address else builder.walletAddress),
        proposalKey = ProposalKey(
            address = account.address.base16Value,
            keyId = account.keys.first().id,
            sequenceNum = account.keys.first().sequenceNumber,
        ),
        refBlock = FlowApi.get().getLatestBlockHeader().id.base16Value,
    )
}

private fun FlowTransaction.buildPayerSignable(
): PayerSignable? {
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
                address = GasConfig.payer().address.toAddress(),
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

private fun Voucher.toFlowTransaction(): FlowTransaction {
    val transaction = this
    var tx = flowTransaction {
        script { transaction.cadence.orEmpty() }

        arguments = transaction.arguments.orEmpty().map {
            val jsonObject = JsonObject()
            jsonObject.addProperty("type", it.type)
            jsonObject.addProperty("value", it.value)
            jsonObject.toString().toByteArray()
        }.map { FlowArgument(it) }.toMutableList()

        referenceBlockId = FlowId(transaction.refBlock.orEmpty())

        gasLimit = computeLimit ?: 9999

        proposalKey {
            address = FlowAddress(transaction.proposalKey.address.orEmpty())
            keyIndex = transaction.proposalKey.keyId ?: 0
            sequenceNumber = transaction.proposalKey.sequenceNum ?: 0
        }

        authorizers(mutableListOf(FlowAddress(transaction.proposalKey.address.orEmpty())))

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

private fun sendTransactionNormal(
    script: String,
    fromAddress: String,
    args: CadenceArgumentsBuilder.() -> Unit,
): String {
    val latestBlockId = FlowApi.get().getLatestBlockHeader().id

    val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress.toAddress()))!!

    val tx = flowTransaction {
        script { script.replaceFlowAddress() }

        arguments { args.builder().toFlowArguments() }

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