package io.outblock.lilico.page.nft.nftlist.utils

import io.outblock.lilico.BuildConfig
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit

class NftGridRequester {
    private val limit = 25
    private var offset = 0

    private var count = -1

    private val service by lazy { retrofit().create(ApiService::class.java) }

    private val dataList = mutableListOf<Nft>()

    fun cachedNfts() = cache().grid().read() ?: NftList()

    suspend fun request(): NftList {
        resetOffset()
        val response = service.nftList(address(), offset, limit)
        if (response.status > 200) {
            throw Exception("request grid list error: $response")
        }

        dataList.clear()

        if (response.data == null) {
            count = 0
            return NftList()
        }

        count = response.data.nftCount

        cache().grid().cache(NftList(response.data.nfts.orEmpty()))

        dataList.addAll(response.data.nfts.orEmpty())

        return NftList(list = response.data.nfts.orEmpty(), count = response.data.nftCount)
    }

    suspend fun nextPage(): NftList {
        offset += limit
        val response = service.nftList(address(), offset, limit)
        response.data ?: return NftList()
        count = response.data.nftCount

        dataList.addAll(response.data.nfts.orEmpty())

        return NftList(list = response.data.nfts.orEmpty(), count = response.data.nftCount)
    }

    fun count() = count

    fun dataList() = dataList.toList()

    fun haveMore() = count >= 0 && offset < count

    private fun resetOffset() = apply { offset = 0 }
    private fun cache() = NftCache(address())

    private fun address(): String {
        if (BuildConfig.DEBUG) {
            return "0xccea80173b51e028"
        }
        return walletCache().read()?.primaryWalletAddress().orEmpty()
    }

}