package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.marshall
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.formatCadence
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logv
import io.outblock.lilico.wallet.toAddress

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
    val result = CADENCE_QUERY_ADDRESS_BY_DOMAIN_FIND.executeScript {
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
    logd(TAG, "cadenceCheckTokenEnabled() address:${coin.address()}")
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return null
    val result = coin.formatCadence(CADENCE_CHECK_TOKEN_IS_ENABLED).executeScript {
        arg { address(walletAddress) }
    }
    logd(TAG, "cadenceCheckTokenEnabled response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseBool()
}

fun cadenceCheckTokenListEnabled(coins: List<FlowCoin>): List<Boolean>? {
    logd(TAG, "cadenceCheckTokenListEnabled()")
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return null

    val tokenImports = coins.map { it.formatCadence("import <Token> from <TokenAddress>") }.joinToString("\r\n") { it }

    val tokenFunctions = coins.map {
        it.formatCadence(
            """
        pub fun check<Token>Vault(address: Address) : Bool {
            let receiver: Bool = getAccount(address)
            .getCapability<&<Token>.Vault{FungibleToken.Receiver}>(<TokenReceiverPath>)
            .check()
            let balance: Bool = getAccount(address)
             .getCapability<&<Token>.Vault{FungibleToken.Balance}>(<TokenBalancePath>)
             .check()
             return receiver && balance
          }
    """.trimIndent()
        )
    }.joinToString("\r\n") { it }

    val tokenCalls = coins.map {
        it.formatCadence(
            """
        check<Token>Vault(address: address)
    """.trimIndent()
        )
    }.joinToString(",") { it }

    val cadence = """
        import FungibleToken from 0xFungibleToken
        <TokenImports>
        <TokenFunctions>
        pub fun main(address: Address) : [Bool] {
            return [<TokenCall>]
        }
    """.trimIndent().replace("<TokenFunctions>", tokenFunctions)
        .replace("<TokenImports>", tokenImports)
        .replace("<TokenCall>", tokenCalls)


    val result = cadence.executeScript {
        arg { address(walletAddress) }
    }
    logd(TAG, "cadenceCheckTokenListEnabled response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseBoolList()
}

fun cadenceQueryTokenBalance(coin: FlowCoin): Float? {
    val walletAddress = walletCache().read()?.primaryWalletAddress()?.toAddress() ?: return 0f
    logd(TAG, "cadenceQueryTokenBalance()")
    val result = coin.formatCadence(CADENCE_GET_BALANCE).executeScript {
        arg { address(walletAddress) }
    }
    logd(TAG, "cadenceQueryTokenBalance response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseFloat()
}

fun cadenceEnableToken(coin: FlowCoin): String? {
    logd(TAG, "cadenceEnableToken()")
    val transactionId = coin.formatCadence(CADENCE_ADD_TOKEN).transactionByMainWallet {}
    logd(TAG, "cadenceEnableToken() transactionId:$transactionId")
    return transactionId
}

fun cadenceTransferToken(fromAddress: String, toAddress: String, amount: Float): String? {
    logd(TAG, "cadenceTransferToken()")
    val transactionId = CADENCE_TRANSFER_TOKEN.transaction(fromAddress) {
        arg { ufix64(amount) }
        arg { address(toAddress.toAddress()) }
    }
    logd(TAG, "cadenceTransferToken() transactionId:$transactionId")
    return transactionId
}

fun cadenceNftCheckEnabled(nft: NftCollection): Boolean? {
    logd(TAG, "cadenceNftCheckEnabled() nft:${nft.name}")
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return null
    val result = nft.formatCadence(CADENCE_NFT_CHECK_ENABLED).executeScript {
        arg { address(walletAddress) }
    }
    logd(TAG, "cadenceNftCheckEnabled response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseBool()
}

fun cadenceNftEnabled(nft: NftCollection): String? {
    logd(TAG, "cadenceNftEnabled() nft:${nft.name}")
    val transactionId = nft.formatCadence(CADENCE_NFT_ENABLE).transactionByMainWallet {}
    logd(TAG, "cadenceEnableToken() transactionId:$transactionId")
    return transactionId
}

fun cadenceNftListCheckEnabled(nfts: List<NftCollection>): List<Boolean>? {
    logd(TAG, "cadenceNftListCheckEnabled()")
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return null

    val tokenImports = nfts.map { nft -> nft.formatCadence("import <Token> from <TokenAddress>") }.joinToString("\r\n") { it }
    val tokenFunctions = nfts.map { nft ->
        nft.formatCadence(
            """
            pub fun check<Token>Vault(address: Address) : Bool {
                let account = getAccount(address)
                let vaultRef = account
                .getCapability<&{NonFungibleToken.CollectionPublic}>(<TokenCollectionPublicPath>)
                .check()
                return vaultRef
            }
        """.trimIndent()
        )
    }.joinToString("\r\n") { it }

    val tokenCalls = nfts.map { nft ->
        nft.formatCadence(
            """
        check<Token>Vault(address: address)
        """.trimIndent()
        )
    }.joinToString(",") { it }


    val cadence = """
        import NonFungibleToken from 0xNonFungibleToken
          <TokenImports>
          <TokenFunctions>
          pub fun main(address: Address) : [Bool] {
            return [<TokenCall>]
        }
    """.trimIndent().replace("<TokenFunctions>", tokenFunctions)
        .replace("<TokenImports>", tokenImports)
        .replace("<TokenCall>", tokenCalls)

    val result = cadence.executeScript {
        arg { address(walletAddress) }
    }

    logd(TAG, "cadenceNftListCheckEnabled response:${String(result?.bytes ?: byteArrayOf())}")
    return result?.parseBoolList()
}

fun cadenceTransferNft(toAddress: String, nft: Nft): String? {
    logd(TAG, "cadenceTransferNft()")
    val transactionId = nft.formatCadence(CADENCE_NFT_TRANSFER).transactionByMainWallet {
        arg { address(toAddress.toAddress()) }
        arg { uint64(nft.id.tokenId) }
    }
    logd(TAG, "cadenceTransferNft() transactionId:$transactionId")
    return transactionId
}

private fun String.executeScript(block: ScriptBuilder.() -> Unit): FlowScriptResponse? {
    logv(TAG, "executeScript:\n${Flow.DEFAULT_ADDRESS_REGISTRY.processScript(this, chainId = Flow.DEFAULT_CHAIN_ID)}")
    return try {
        FlowApi.get().simpleFlowScript {
            script { this@executeScript.trimIndent().replaceFlowAddress() }
            block()
        }
    } catch (e: Throwable) {
        loge(e)
        return null
    }
}

private fun String.transactionByMainWallet(arguments: FlowArgumentsBuilder.() -> Unit): String? {
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return null
    return this.transaction(walletAddress, arguments)
}

private fun String.transaction(fromAddress: String, arguments: FlowArgumentsBuilder.() -> Unit): String? {
    return sendFlowTransaction(this, fromAddress, arguments)
}