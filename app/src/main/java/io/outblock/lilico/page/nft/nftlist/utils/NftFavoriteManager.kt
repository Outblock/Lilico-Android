package io.outblock.lilico.page.nft.nftlist.utils

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.cacheFile
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.nftWalletAddress
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object NftFavoriteManager {

    private val listeners = CopyOnWriteArrayList<WeakReference<OnNftFavoriteChangeListener>>()

    private val service by lazy { retrofit().create(ApiService::class.java) }

    private val favoriteList = mutableListOf<Nft>()

    fun addOnNftSelectionChangeListener(listener: OnNftFavoriteChangeListener) {
        listeners.add(WeakReference(listener))
    }

    suspend fun request() {
        // TODO read from cache then dispatch
        service.getNftFavorite()
        // TODO save to cache then dispatch
    }

    fun addFavorite(contractName: String?, tokenId: String?) {
        contractName ?: return
        tokenId ?: return
        ioScope {
            service.addNftFavorite(contractName, tokenId)
            request()
        }
    }

    fun removeFavorite(contractName: String?, tokenId: String?) {
        contractName ?: return
        tokenId ?: return
        ioScope {
            val favorites = favoriteList().toMutableList()
            favorites.removeAll { it.contractName() == contractName && it.tokenId() == tokenId }
            updateFavorite(favorites.map { it.uniqueId() })
            request()
        }
    }

    private suspend fun updateFavorite(ids: List<String>) {
        service.updateFavorite(uniqueIds = ids.joinToString(","))
    }

    fun favoriteList() = if (favoriteList.isEmpty()) cache().read()?.nfts.orEmpty() else favoriteList.toList()

    private fun cache() = CacheManager("${nftWalletAddress()}_nft_favorite".cacheFile(), FavoriteCache::class.java)

    private fun dispatchListener(ids: List<Nft>) {
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.forEach { it.get()?.onNftFavoriteChange(ids) }
        }
    }

}

interface OnNftFavoriteChangeListener {
    fun onNftFavoriteChange(nfts: List<Nft>)
}

private data class FavoriteCache(
    @SerializedName("ids")
    val nfts: List<Nft>,
)