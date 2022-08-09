package io.outblock.lilico.page.nft.nftlist.model

import io.outblock.lilico.network.model.Nft

data class CollectionItemModel(
    val name: String?,
    val count: Int,
    val address: String,
    val nfts: List<Nft>? = null,
    var isSelected: Boolean = false,
)