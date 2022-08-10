package io.outblock.lilico.page.nft.nftlist.utils

import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.cacheFile
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.NftCollections
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.nftWalletAddress

class NftFavoriteRequester {

    private val service by lazy { retrofit().create(ApiService::class.java) }

    fun cachedFavorite() = cache().read()

    suspend fun request() {
        service.getNftFavorite()
    }

    suspend fun addFavorite(contractName: String, tokenId: String) {
        service.addNftFavorite(contractName, tokenId)
    }

    suspend fun updateFavorite() {
//        service.updateFavorite(contractName,tokenId)
    }

    private fun cache() = CacheManager("${nftWalletAddress()}_nft_favorite".cacheFile(), NftCollections::class.java)

}