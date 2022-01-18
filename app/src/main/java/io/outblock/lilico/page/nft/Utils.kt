package io.outblock.lilico.page.nft

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.toCoverUrl
import io.outblock.lilico.utils.updateNftSelectionsPref

val nftListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem.uniqueId() == newItem.uniqueId()
        }

        return false
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem == newItem
        }
//        if(oldItem.javaClass == newItem.javaClass){
//            if(oldItem is NFTTitleModel){
//                return oldItem == newItem
//            }
//        }

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