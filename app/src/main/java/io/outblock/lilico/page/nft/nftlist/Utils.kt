package io.outblock.lilico.page.nft.nftlist

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.model.CollectionItemModel
import io.outblock.lilico.page.nft.nftlist.model.CollectionTabsModel
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.utils.loge

val nftListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is NFTItemModel && newItem is NFTItemModel) {
            return oldItem.nft.uniqueId() == newItem.nft.uniqueId()
        }

        if (oldItem is CollectionTabsModel && newItem is CollectionTabsModel) {
            return true
        }

        if (oldItem is NftSelections && newItem is NftSelections) {
            return true
        }

        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is CollectionTabsModel && newItem is CollectionTabsModel) {
            return true
        }

        if (oldItem is NftSelections && newItem is NftSelections) {
            return true
        }

        return oldItem == newItem
    }
}

fun Nft.cover(): String? {
    return postMedia.image ?: postMedia.video
}

fun Nft.name(): String? {
    if (!postMedia.title.isNullOrBlank()) {
        return postMedia.title
    }
    val config = NftCollectionConfig.get(contract.address) ?: return null
    return "${config.name} #${id.tokenId}"
}

fun Nft.desc(): String? {
    return postMedia.description ?: metadata.metadata.firstOrNull { it.name == "description" }?.value
}

fun Nft.video(): String? {
    return postMedia.video
}

fun Nft.isSameNft(other: Nft): Boolean {
    return contract.address == other.contract.address && id.tokenId == other.id.tokenId
}

fun List<Nft>.parseToCollectionList(): List<CollectionItemModel> {
    val collectionMap = mutableMapOf<String, MutableList<Nft>>()

    forEach {
        val list = collectionMap[it.contract.address] ?: mutableListOf()
        list.add(it)
        collectionMap[it.contract.address] = list
    }
    return collectionMap.filter { NftCollectionConfig.get(it.key) != null }.map {
        val nft = it.value.first()
        CollectionItemModel(
            name = nft.contract.name,
            count = it.value.size,
            address = it.key,
            nfts = it.value,
        )
    }.sortedByDescending { it.count }
}

fun MutableList<Nft>.removeEmpty(): MutableList<Nft> {
    removeAll { it.name().isNullOrBlank() || it.uniqueId().isBlank() || NftCollectionConfig.get(it.contract.address) == null }
    return this
}

suspend fun requestNftListFromServer(address: String): NFTListData? {
    val limit = 25
    val service = retrofit().create(ApiService::class.java)
    val resp = service.nftList(address, 0, limit)
    val list = mutableListOf<Nft>()

    val data = resp.data ?: return null
    val count = data.nftCount
    list.addAll(data.nfts.orEmpty())

    if (count > limit) {
        var index = data.nfts?.size ?: 0
        val exception = runCatching {
            while (index <= count) {
                val r = service.nftList(address, index, index + limit)
                list.addAll(r.data?.nfts.orEmpty())
                index += limit
            }
        }
        loge(exception.exceptionOrNull())
    }

    return data.apply {
        nfts = list
    }
}

fun Nft.websiteUrl(walletAddress: String) = "https://lilico.app/nft/$walletAddress/${contract.address}/${contract.name}?tokenId=${id.tokenId}"

fun findSwipeRefreshLayout(view: View): SwipeRefreshLayout? {
    if (view.parent == null) return null
    if (view.parent is SwipeRefreshLayout) return (view.parent as SwipeRefreshLayout)

    return findSwipeRefreshLayout(view.parent as View)
}