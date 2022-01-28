package io.outblock.lilico.page.nft.model

import io.outblock.lilico.network.model.Nft

data class NFTItemModel(
    val index: Int,
    val nft: Nft,
)