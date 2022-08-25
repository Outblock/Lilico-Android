package io.outblock.lilico.page.nft.nftlist

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.nftlist.model.*
import java.net.URLEncoder
import kotlin.math.min

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
    var image = postMedia.image

    if (!image.isNullOrBlank() && postMedia.isSvg == "true") {
        image = "https://lilico.app/api/svg2png?url=${URLEncoder.encode(image, "UTF-8")}"
    }

    return image ?: video()
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

fun Nft.title(): String? = postMedia.title

fun Nft.isSameNft(other: Nft): Boolean {
    return contract.address == other.contract.address && id.tokenId == other.id.tokenId
}

fun Nft.websiteUrl(walletAddress: String) = "https://lilico.app/nft/$walletAddress/${contract.address}/${contract.name}?tokenId=${id.tokenId}"

fun findSwipeRefreshLayout(view: View): SwipeRefreshLayout? {
    if (view.parent == null) return null
    if (view.parent is SwipeRefreshLayout) return (view.parent as SwipeRefreshLayout)

    return findSwipeRefreshLayout(view.parent as View)
}

fun findParentAppBarLayout(view: View): AppBarLayout? {
    if (view.parent == null) return null
    if (view.parent is AppBarLayout) return (view.parent as AppBarLayout)

    return findParentAppBarLayout(view.parent as View)
}

fun isSingleNftItem(model: Any): Boolean {
    return model is NFTCountTitleModel || model is HeaderPlaceholderModel || model is NFTTitleModel || model is NftSelections
      || model is CollectionTitleModel || model is CollectionItemModel || model is CollectionTabsModel || model is NftLoadMoreModel
}

fun nftWalletAddress(): String {
//    if (BuildConfig.DEBUG) {
//        return "0x53f389d96fb4ce5e"
//    }
    return walletCache().read()?.primaryWalletAddress().orEmpty()
}

fun Nft.isDomain() = media?.firstOrNull { it.uri.contains("flowns.org") && it.uri.contains(".meow") } != null

internal fun generateEmptyNftPlaceholders(count: Int) = (0 until min(count, 12)).map { NftItemShimmerModel() }