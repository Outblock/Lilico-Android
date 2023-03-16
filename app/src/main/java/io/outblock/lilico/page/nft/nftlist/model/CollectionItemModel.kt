package io.outblock.lilico.page.nft.nftlist.model

import io.outblock.lilico.manager.config.NftCollection

data class CollectionItemModel(
    val count: Int,
    val collection: NftCollection,
    var isSelected: Boolean = false,
)