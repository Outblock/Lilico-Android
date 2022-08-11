package io.outblock.lilico.page.nft.nftlist

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.nftlist.model.*

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
    return postMedia.description ?: metadata.metadata?.firstOrNull { it.name == "description" }?.value
}

fun Nft.video(): String? {
    return postMedia.video
}

fun Nft.isSameNft(other: Nft): Boolean {
    return contract.address == other.contract.address && id.tokenId == other.id.tokenId
}

fun Nft.websiteUrl(walletAddress: String) = "https://lilico.app/nft/$walletAddress/${contract.address}/${contract.name}?tokenId=${id.tokenId}"

fun findSwipeRefreshLayout(view: View): SwipeRefreshLayout? {
    if (view.parent == null) return null
    if (view.parent is SwipeRefreshLayout) return (view.parent as SwipeRefreshLayout)

    return findSwipeRefreshLayout(view.parent as View)
}

fun getNftByIdFromCache(uniqueId: String): Nft? {
    return nftListCache(walletCache().read()?.primaryWalletAddress()).read()?.nfts?.firstOrNull { it.uniqueId() == uniqueId }
}

fun isSingleNftItem(model: Any): Boolean {
    return model is NFTCountTitleModel || model is HeaderPlaceholderModel || model is NFTTitleModel || model is NftSelections
      || model is CollectionTitleModel || model is CollectionItemModel || model is CollectionTabsModel || model is NftLoadMoreModel
}

fun nftWalletAddress(): String {
    if (BuildConfig.DEBUG) {
        return "0x53f389d96fb4ce5e"
    }
    return walletCache().read()?.primaryWalletAddress().orEmpty()
}