package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.marshall
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.formatCadence
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.wallet.getPrivateKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

private const val TAG = "CadenceExecutor"

fun cadenceQueryAddressByDomainFlowns(domain: String, root: String = "fn"): String? {
    logd(TAG, "cadenceQueryAddressByDomainFlowns()")
    val result = CADENCE_QUERY_ADDRESS_BY_DOMAIN_FLOWNS.executeScript {
        arg { marshall { string(domain) } }
        arg { marshall { string(root) } }
    }
    logd(TAG, "cadenceQueryAddressByDomainFlowns response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseSearchAddress()
}

fun cadenceQueryDomainByAddressFlowns(address: String): FlowScriptResponse? {
    logd(TAG, "cadenceQueryDomainByAddressFlowns()")
    val result = CADENCE_QUERY_DOMAIN_BY_ADDRESS_FLOWNS.executeScript {
        arg { address(address) }
    }
    logd(TAG, "cadenceQueryDomainByAddressFlowns response:${String(result?.bytes ?: byteArrayOf())}")
    return result
}

fun cadenceQueryAddressByDomainFind(domain: String): String? {
    logd(TAG, "cadenceQueryAddressByDomainFind()")
    val result = CADENCE_QUERY_ADDRESS_BY_ADDRESS_FIND.executeScript {
        arg { marshall { string(domain) } }
    }
    logd(TAG, "cadenceQueryAddressByDomainFind response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseSearchAddress()
}

fun cadenceQueryDomainByAddressFind(address: String): FlowScriptResponse? {
    logd(TAG, "cadenceQueryDomainByAddressFind()")
    val result = CADENCE_QUERY_DOMAIN_BY_ADDRESS_FIND.executeScript {
        arg { address(address) }
    }
    logd(TAG, "cadenceQueryDomainByAddressFind response:${String(result?.bytes ?: byteArrayOf())}")
    return result
}

fun cadenceCheckTokenEnabled(coin: FlowCoin): Boolean? {
    logd(TAG, "cadenceCheckTokenEnabled()")
    val result = coin.formatCadence(CADENCE_CHECK_TOKEN_IS_ENABLED).executeScript {
        arg { address(coin.address()) }
    }
    logd(TAG, "cadenceCheckTokenEnabled response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseBool()
}

fun cadenceQueryTokenBalance(coin: FlowCoin): Float? {
    val walletAddress = walletCache().read()?.primaryWallet()?.blockchain?.first()?.address ?: return 0f
    logd(TAG, "cadenceQueryTokenBalance()")
    val result = coin.formatCadence(CADENCE_GET_BALANCE).executeScript {
        arg { address(walletAddress) }
    }
    logd(TAG, "cadenceQueryTokenBalance response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseFloat()
}

fun cadenceEnableToken(coin: FlowCoin): String? {
    logd(TAG, "cadenceEnableToken()")
    val transactionId = coin.formatCadence(CADENCE_ADD_TOKEN).transactionByMainWallet {
        arg { address(coin.address()) }
    }
    logd(TAG, "cadenceEnableToken() transactionId:$transactionId")
    return transactionId
}

fun cadenceTransferToken(fromAddress: String, toAddress: String, amount: Float): String? {
    logd(TAG, "cadenceTransferToken()")
    val transactionId = CADENCE_TRANSFER_TOKEN.transaction(fromAddress) {
        arg { ufix64(amount) }
        arg { address(toAddress) }
    }
    logd(TAG, "cadenceTransferToken() transactionId:$transactionId")
    return transactionId
}

private fun String.executeScript(block: ScriptBuilder.() -> Unit): FlowScriptResponse? {
    return try {
        FlowApi.get().simpleFlowScript {
            script { this@executeScript.trimIndent() }
            block()
        }
    } catch (e: Throwable) {
        loge(e)
        return null
    }
}

private fun String.transactionByMainWallet(arguments: FlowArgumentsBuilder.() -> Unit): String? {
    val walletAddress = walletCache().read()?.primaryWallet()?.blockchain?.first()?.address ?: return null
    return this.transaction(walletAddress, arguments)
}

private fun String.transaction(fromAddress: String, arguments: FlowArgumentsBuilder.() -> Unit): String? {
    updateSecurityProvider()
    try {
        val latestBlockId = FlowApi.get().getLatestBlockHeader().id

        val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress))!!

        val tx = flowTransaction {
            script { this@transaction }

            arguments { arguments() }

            referenceBlockId = latestBlockId
            gasLimit = 100

            proposalKey {
                address = payerAccount.address
                keyIndex = payerAccount.keys[0].id
                sequenceNumber = payerAccount.keys[0].sequenceNumber.toLong()
            }

            authorizers(mutableListOf(FlowAddress(fromAddress)))
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
    } catch (e: Exception) {
        loge(TAG, e)
        return null
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