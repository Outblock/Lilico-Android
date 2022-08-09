package io.outblock.lilico.page.nft.nftlist.utils

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.cacheFile
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.NftCollections


class NftCache(
    private val address: String
) {

    fun grid(): CacheManager<NftList> {
        return CacheManager("${address}_nft_grid".cacheFile(), NftList::class.java)
    }

    fun collection(): CacheManager<NftCollections> {
        return CacheManager("${address}_nft_collection".cacheFile(), NftCollections::class.java)
    }

    fun list(collectionAddress: String): CacheManager<NftList> {
        return CacheManager("${address}_${collectionAddress}_nft_list".cacheFile(), NftList::class.java)
    }

    fun findNftById(uniqueId: String): Nft? {
        return grid().read()?.list?.firstOrNull { it.uniqueId() == uniqueId } ?: findNftFromCollection(uniqueId)
    }

    private fun findNftFromCollection(uniqueId: String): Nft? {
        val collections = collection().read()?.collections?.mapNotNull { it.collection?.address() } ?: return null
        for (collection in collections) {
            val nfts = list(collection).read()?.list ?: continue
            return nfts.firstOrNull { it.uniqueId() == uniqueId } ?: continue
        }
        return null
    }
}

data class NftList(
    @SerializedName("list")
    val list: List<Nft> = emptyList(),
    @SerializedName("count")
    val count: Int = 0,
)