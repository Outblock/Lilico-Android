package io.outblock.lilico.cache

import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.network.model.WalletListData


const val CACHE_NFT_LIST = "nft_list"
const val CACHE_WALLET = "wallet"

fun nftListCache(address: String?): CacheManager<NFTListData> {
    return CacheManager("${address}_$CACHE_NFT_LIST", NFTListData::class.java)
}

fun walletCache(): CacheManager<WalletListData> {
    return CacheManager(CACHE_WALLET, WalletListData::class.java)
}