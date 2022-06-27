//package io.outblock.lilico.manager.flowjvm.transaction
//
//import com.google.gson.Gson
//import com.google.gson.JsonObject
//import com.nftco.flow.sdk.*
//import com.nftco.flow.sdk.crypto.Crypto
//import io.outblock.lilico.manager.config.GasConfig
//import io.outblock.lilico.manager.flowjvm.CadenceArgumentsBuilder
//import io.outblock.lilico.manager.flowjvm.FlowApi
//import io.outblock.lilico.manager.flowjvm.builder
//import io.outblock.lilico.manager.flowjvm.replaceFlowAddress
//import io.outblock.lilico.network.functions.FUNCTION_SIGN_AS_PAYER
//import io.outblock.lilico.network.functions.executeFunction
//import io.outblock.lilico.utils.logd
//import io.outblock.lilico.utils.loge
//import io.outblock.lilico.utils.logv
//import io.outblock.lilico.wallet.getPrivateKey
//import io.outblock.lilico.wallet.toAddress
//import org.bouncycastle.jce.provider.BouncyCastleProvider
//import java.security.Provider
//import java.security.Security
//
//private const val TAG = "FlowTransaction"
//
//suspend fun sendFlowTransaction(
//    candece: String,
//    fromAddress: String,
//    arguments: CadenceArgumentsBuilder.() -> Unit,
//): String? {
//    logv(TAG, "transaction cadence:$candece")
//    updateSecurityProvider()
//
//    return try {
//        if (GasConfig.isFreeGas()) {
//            sendTransactionFreeGas(candece, fromAddress, arguments)
//        } else sendTransactionNormal(candece, fromAddress, arguments)
//    } catch (e: Exception) {
//        loge(e)
//        null
//    }
//}
//
//private fun buildTransaction(
//    cadence: String,
//    walletAddress: String,
//    payer: String,
//    args: CadenceArgumentsBuilder.() -> Unit,
//): FlowTransaction? {
//
//    val walletAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(walletAddress.toAddress())) ?: return null
//
//    return flowTransaction {
//        script { cadence.replaceFlowAddress() }
//
//        arguments { args.builder().toFlowArguments() }
//
//        referenceBlockId = FlowApi.get().getLatestBlockHeader().id
//        gasLimit = 100
//
//        proposalKey {
//            address = walletAccount.address
//            keyIndex = walletAccount.keys[0].id
//            sequenceNumber = walletAccount.keys[0].sequenceNumber.toLong()
//        }
//
//        authorizers(mutableListOf(FlowAddress(walletAddress.toAddress())))
//
//        payerAddress = FlowAddress(payer.toAddress())
//
//        payloadSignature(
//            FlowAddress(walletAddress), 0, signer = Crypto.getSigner(
//                privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
//                hashAlgo = HashAlgorithm.SHA2_256
//            )
//        )
//    }
//}
//
//private suspend fun sendTransactionFreeGas(
//    script: String,
//    fromAddress: String,
//    args: CadenceArgumentsBuilder.() -> Unit,
//): String? {
//
//    val signable = buildSignable(script, fromAddress, args) ?: return null
//
//    val str = executeFunction(FUNCTION_SIGN_AS_PAYER, data = signable)
//
//    val sign = Gson().fromJson(str, SignPayerResponse::class.java).envelopeSigs
//
//    logd(TAG, "response:$str")
//
//    return ""
//}
//
//private fun buildSignable(
//    script: String,
//    fromAddress: String,
//    args: CadenceArgumentsBuilder.() -> Unit,
//): PayerSignable? {
//    val payerAddress = GasConfig.payer().address
//
//    val account = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress.toAddress())) ?: return null
//    val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(payerAddress.toAddress())) ?: return null
//
//    val voucher = Voucher(
//        cadence = script.replaceFlowAddress(),
//        refBlock = FlowApi.get().getLatestBlockHeader().id.base16Value,
//        computeLimit = 9999,
//        arguments = args.builder().build().map { AsArgument(it.type, it.value.toString()) },
//        proposalKey = ProposalKey(
//            address = fromAddress,
//            keyId = account.keys.first().id,
//            sequenceNum = account.keys.first().sequenceNumber,
//        ),
//        payer = GasConfig.payer().address,
//        authorizers = listOf(fromAddress),
//        envelopeSigs = listOf(
//            Singature(
//                address = GasConfig.payer().address,
//                keyId = payerAccount.keys.first().id,
//            )
//        ),
//    )
//
//    val tx = signable.toFlowTransaction(payerAccount)
//
//    signable.transaction.payloadSigs = listOf(
//        Singature(
//            address = fromAddress,
//            keyId = account.keys.first().id,
//            sig = tx.payloadSignatures.first().signature.base16Value,
//        )
//    )
//
//    signable.message = PayerSignable.Message(
//        (DomainTag.TRANSACTION_DOMAIN_TAG + tx.canonicalAuthorizationEnvelope).bytesToHex()
//    )
//
//    return signable
//}
//
//private fun Voucher.toFlowTransaction(): FlowTransaction {
//    val transaction = this
//    return flowTransaction {
//        script { transaction.cadence.orEmpty() }
//
//        arguments = transaction.arguments.orEmpty().map {
//            val jsonObject = JsonObject()
//            jsonObject.addProperty("type", it.type)
//            jsonObject.addProperty("value", it.value)
//            jsonObject.toString().toByteArray()
//        }.map { FlowArgument(it) }.toMutableList()
//
//        referenceBlockId = FlowId(transaction.refBlock.orEmpty())
//
//        gasLimit = computeLimit ?: 9999
//
//        proposalKey {
//            address = FlowAddress(transaction.proposalKey.address.orEmpty())
//            keyIndex = transaction.proposalKey.keyId ?: 0
//            sequenceNumber = transaction.proposalKey.sequenceNum ?: 0
//        }
//
//        authorizers(mutableListOf(FlowAddress(transaction.proposalKey.address.orEmpty())))
//        payerAddress = FlowAddress(transaction.payer.orEmpty())
//
//        payloadSignature(
//            FlowAddress(transaction.proposalKey.address.orEmpty()), 0, signer = Crypto.getSigner(
//                privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
//                hashAlgo = HashAlgorithm.SHA2_256
//            )
//        )
//    }
//}
//
//private fun sendTransactionNormal(
//    script: String,
//    fromAddress: String,
//    args: CadenceArgumentsBuilder.() -> Unit,
//): String {
//    val latestBlockId = FlowApi.get().getLatestBlockHeader().id
//
//    val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress.toAddress()))!!
//
//    val tx = flowTransaction {
//        script { script.replaceFlowAddress() }
//
//        arguments { args.builder().toFlowArguments() }
//
//        referenceBlockId = latestBlockId
//        gasLimit = 100
//
//        proposalKey {
//            address = payerAccount.address
//            keyIndex = payerAccount.keys[0].id
//            sequenceNumber = payerAccount.keys[0].sequenceNumber.toLong()
//        }
//
//        authorizers(mutableListOf(FlowAddress(fromAddress.toAddress())))
//        payerAddress = payerAccount.address
//
//        signatures {
//            signature {
//                address = payerAccount.address
//                keyIndex = 0
//                signer = Crypto.getSigner(
//                    privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
//                    hashAlgo = HashAlgorithm.SHA2_256
//                )
//            }
//        }
//    }
//
//    val txID = FlowApi.get().sendTransaction(tx)
//    logd(TAG, "transaction id:$${txID.bytes.bytesToHex()}")
//    return txID.bytes.bytesToHex()
//}
//
///**
// * fix: java.security.NoSuchAlgorithmException: no such algorithm: ECDSA for provider BC
// */
//private fun updateSecurityProvider() {
//    // Web3j will set up the provider lazily when it's first used.
//    val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) ?: return
//    if (provider.javaClass == BouncyCastleProvider::class.java) {
//        // BC with same package name, shouldn't happen in real life.
//        return
//    }
//    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
//    Security.insertProviderAt(BouncyCastleProvider(), 1)
//}