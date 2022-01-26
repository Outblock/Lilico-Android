package io.outblock.lilico.page.nft

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.model.CollectionItemModel
import io.outblock.lilico.page.nft.model.CollectionTabsModel
import io.outblock.lilico.page.nft.model.NFTItemModel
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
    var url = media?.uri?.toCoverUrl()
    if (url.isNullOrEmpty()) {
        url = metadata.metadata.firstOrNull { it.name == "image" }?.value
    }
    return url
}

fun Nft.name(): String? {
    return title ?: metadata.metadata.firstOrNull { it.name == "title" }?.value ?: contract.name
}

fun Nft.desc(): String? {
    return description ?: metadata.metadata.firstOrNull { it.name == "description" }?.value
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
    return collectionMap.map {
        val nft = it.value.first()
        CollectionItemModel(
            name = nft.contract.name,
            count = it.value.size,
            address = it.key,
            nfts = it.value,
        )
    }.sortedByDescending { it.count }
}