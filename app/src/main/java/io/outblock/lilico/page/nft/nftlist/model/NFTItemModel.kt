package io.outblock.lilico.page.nft.nftlist.model

import io.outblock.lilico.network.model.Nft

data class NFTItemModel(
    val index: Int = 0,
    val nft: Nft,
)