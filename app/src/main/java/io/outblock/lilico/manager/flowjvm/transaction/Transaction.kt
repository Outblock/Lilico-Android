package io.outblock.lilico.manager.flowjvm.transaction

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
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logv
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.wallet.toAddress
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

private const val TAG = "FlowTransaction"

suspend fun sendFlowTransaction(
    script: String,
    fromAddress: String,
    arguments: CadenceArgumentsBuilder.() -> Unit,
): String? {
    logv(TAG, "transaction script:$script")
    updateSecurityProvider()

    return try {
        if (GasConfig.isFreeGas()) {
            sendTransactionFreeGas(script, fromAddress, arguments)
        } else sendTransactionNormal(script, fromAddress, arguments)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

private suspend fun sendTransactionFreeGas(
    script: String,
    fromAddress: String,
    args: CadenceArgumentsBuilder.() -> Unit,
): String {
    val payerAddress = GasConfig.payer().address
    val proposalAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress.toAddress()))!!
    val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(payerAddress.toAddress()))!!

    val tx = toFlowTransaction(script, fromAddress, args)
    val response = executeFunction<SignPayerResponse>(
        FUNCTION_SIGN_AS_PAYER, data = Signable(
            transaction = Signable.Transaction(
                cadence = script,
                refBlock = FlowApi.get().getLatestBlockHeader().id.base16Value,
                computeLimit = 9999,
                arguments = args.builder().build().map { Signable.Transaction.Argument(it.type, it.value.toString()) },
                proposalKey = Signable.Transaction.ProposalKey(
                    address = fromAddress,
                    keyId = proposalAccount.keys.first().id,
                    sequenceNum = proposalAccount.keys.first().sequenceNumber,
                ),
                payer = GasConfig.payer().address,
                authorizers = listOf(fromAddress),
                payloadSigs = listOf(
                    Signable.Transaction.Sig(
                        address = fromAddress,
                        keyId = proposalAccount.keys.first().id,
                        sig = Crypto.getSigner(
                            privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                            hashAlgo = HashAlgorithm.SHA2_256
                        ).signAsTransaction(tx.canonicalPayload).bytesToHex(),
                    )
                ),
                envelopeSigs = listOf(
                    Signable.Transaction.Sig(
                        address = GasConfig.payer().address,
                        keyId = payerAccount.keys.first().id,
                    )
                ),
            ),
            message = Signable.Message(
                Crypto.getSigner(
                    privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                    hashAlgo = HashAlgorithm.SHA2_256
                ).signAsTransaction(tx.canonicalAuthorizationEnvelope).bytesToHex()
            ),
        )
    )

    logd("xxx", "response:$response")

    return ""
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

private fun toFlowTransaction(
    script: String,
    fromAddress: String,
    args: CadenceArgumentsBuilder.() -> Unit,
): FlowTransaction {
    val payer = GasConfig.payer().address

    val proposalAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress.toAddress()))!!
    val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(payer.toAddress()))!!

    return flowTransaction {
        script { script.replaceFlowAddress() }

        arguments { args.builder().toFlowArguments() }

        referenceBlockId = FlowApi.get().getLatestBlockHeader().id
        gasLimit = 9999

        proposalKey {
            address = proposalAccount.address
            keyIndex = proposalAccount.keys[0].id
            sequenceNumber = proposalAccount.keys[0].sequenceNumber.toLong()
        }

        authorizers(mutableListOf(FlowAddress(fromAddress.toAddress())))
        payerAddress = payerAccount.address

        addPayloadSignatures {
            signature(
                payerAccount.address,
                0,
                signer = Crypto.getSigner(
                    privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                    hashAlgo = HashAlgorithm.SHA2_256
                )
            )
        }

//        signatures {
//            signature(
//                FlowAddress(fromAddress.toAddress()),
//                proposalAccount.keys.first().id,
//                Crypto.getSigner(
//                    privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
//                    hashAlgo = HashAlgorithm.SHA2_256
//                )
//            )
//        }
    }
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