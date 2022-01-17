package io.outblock.lilico.page.nft

import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.toCoverUrl

val nftListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem.contract.address == newItem.contract.address
        }

        return true
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem == newItem
        }

        return true
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
    return title ?: metadata.metadata.firstOrNull { it.name == "title" }?.value ?:contract.name
}

fun Nft.desc(): String? {
    return description ?: metadata.metadata.firstOrNull { it.name == "description" }?.value
}