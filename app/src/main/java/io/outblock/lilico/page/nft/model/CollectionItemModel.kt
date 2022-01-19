package io.outblock.lilico.page.nft.model

import io.outblock.lilico.network.model.Nft

class CollectionItemModel(
    val name: String?,
    val count: Int,
    val address: String,
    val nfts: List<Nft>?,
)