package io.outblock.lilico.page.nft.nftlist

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.model.CollectionItemModel
import io.outblock.lilico.page.nft.nftlist.model.CollectionTabsModel
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.utils.toCoverUrl

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
    var url = media?.firstOrNull { it.mimeType == "image" }?.uri?.toCoverUrl()
    if (url.isNullOrEmpty()) {
        url = metadata.metadata.firstOrNull { it.name == "image" }?.value
    }
    return url ?: video()
}

fun Nft.name(): String? {
    if (!title.isNullOrBlank()) {
        return title
    }
    val config = NftCollectionConfig.get(contract.address) ?: return null
    return "${config.name} #${id.tokenId}"
}

fun Nft.desc(): String? {
    return description ?: metadata.metadata.firstOrNull { it.name == "description" }?.value
}

fun Nft.video(): String? {
    val media = media?.firstOrNull { it.mimeType.startsWith("video/") }?.uri?.trim()?.removePrefix("ipfs://")
    if (media.isNullOrBlank()) {
        val arLink = metadata.metadata.firstOrNull { it.name == "arLink" }?.value ?: return null
        return "https://arweave.net/${arLink}"
    }
    return media
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
    val limit = 30
    val service = retrofit().create(ApiService::class.java)
    val resp = service.nftList(address, 0, limit)
    val list = mutableListOf<Nft>()

    val count = resp.data?.nftCount ?: 0
    list.addAll(resp.data?.nfts.orEmpty())

    if (count > limit) {
        var index = resp.data?.nfts?.size ?: 0
        while (index <= count) {
            val r = service.nftList(address, index, index + limit)
            list.addAll(r.data?.nfts.orEmpty())
            index += limit
        }
    }

    return resp.data?.apply {
        nfts = list
    }
}