package io.outblock.lilico.page.nft.nftlist.utils

import io.outblock.lilico.BuildConfig
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.NftCollectionWrapper
import io.outblock.lilico.network.model.NftCollections
import io.outblock.lilico.network.retrofit

class NftListRequester {
    private val limit = 25
    private var offset = 0

    private var count = -1

    private val service by lazy { retrofit().create(ApiService::class.java) }

    private val dataList = mutableListOf<Nft>()

    private val collectionList = mutableListOf<NftCollectionWrapper>()

    fun cacheCollections() = cache().collection().read()?.collections?.sort()

    suspend fun requestCollection(): List<NftCollectionWrapper>? {
        val collectionResponse = service.nftCollections(address())
        if (collectionResponse.status > 200) {
            throw Exception("request nft list error: $collectionResponse")
        }
        val collections = collectionResponse.data?.filter { it.collection?.address()?.isNotBlank() == true }
        collectionList.clear()
        collectionList.addAll(collections ?: listOf())
        cache().collection().cache(NftCollections(collections.orEmpty()))
        return collections?.sort()
    }

    fun cachedNfts(collection: NftCollection) = cache().list(collection.address()).read()?.list

    suspend fun request(collection: NftCollection): List<Nft> {
        resetOffset()
        val response = service.nftsOfCollection(address(), collection.contractName, offset, limit)
        if (response.status > 200) {
            throw Exception("request nft list error: $response")
        }

        dataList.clear()

        if (response.data == null) {
            count = 0
            return emptyList()
        }

        count = response.data.nftCount

        cache().list(collection.address()).cache(NftList(response.data.nfts.orEmpty()))

        dataList.addAll(response.data.nfts.orEmpty())

        return response.data.nfts.orEmpty()
    }

    suspend fun nextPage(collection: NftCollection): List<Nft> {
        offset += limit
        val response = service.nftsOfCollection(address(), collection.contractName, offset, limit)
        response.data ?: return emptyList()
        count = response.data.nftCount

        dataList.addAll(response.data.nfts.orEmpty())

        return response.data.nfts.orEmpty()
    }

    fun count() = count

    fun haveMore() = count >= 0 && offset < count

    private fun resetOffset() = apply {
        offset = 0
        count = -1
    }

    private fun cache() = NftCache(address())

    private fun address(): String {
        if (BuildConfig.DEBUG) {
            return "0x53f389d96fb4ce5e"
        }
        return walletCache().read()?.primaryWalletAddress().orEmpty()
    }

    private fun List<NftCollectionWrapper>?.sort(): List<NftCollectionWrapper>? {
        this ?: return null
        return this.sortedBy { it.collection?.name?.take(1) }.sortedByDescending { it.count }
    }

}