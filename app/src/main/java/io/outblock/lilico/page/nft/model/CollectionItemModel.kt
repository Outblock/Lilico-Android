package io.outblock.lilico.page.nft.model

import io.outblock.lilico.network.model.Nft

data class CollectionItemModel(
    val name: String?,
    val count: Int,
    val address: String,
    val nfts: List<Nft>?,
    var isSelected: Boolean = false,
)