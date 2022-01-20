package io.outblock.lilico.page.nft

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.model.CollectionItemModel
import io.outblock.lilico.page.nft.model.CollectionTabsModel
import io.outblock.lilico.page.nft.model.NFTItemModel
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.toCoverUrl
import io.outblock.lilico.utils.updateNftSelectionsPref

val nftListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is NFTItemModel && newItem is NFTItemModel) {
            return oldItem.nft.uniqueId() == newItem.nft.uniqueId()
        }

        if (oldItem is CollectionTabsModel && newItem is CollectionTabsModel) {
            return true
        }

        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem == newItem
        }

        if (oldItem is CollectionTabsModel && newItem is CollectionTabsModel) {
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

fun addNftToSelection(nft: Nft) {
    ioScope {
        val cache = nftSelectionCache()
        val selections = cache.read() ?: NftSelections(data = emptyList())
        val list = selections.data.toMutableList()
        val isExist = list.firstOrNull { it.contract.address == nft.contract.address && it.id.tokenId == nft.id.tokenId } != null

        if (!isExist) {
            list.add(nft)
        }
        selections.data = list
        cache.cache(selections)
        updateNftSelectionsPref(list.map { it.uniqueId() })
    }
}

fun removeNftFromSelection(nft: Nft) {
    ioScope {
        val cache = nftSelectionCache()
        val selections = cache.read() ?: NftSelections(data = emptyList())
        val list = selections.data.toMutableList()

        list.removeIf { it.isSameNft(nft) }
        selections.data = list
        cache.cache(selections)

        updateNftSelectionsPref(list.map { it.uniqueId() })
    }
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