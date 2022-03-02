package io.outblock.lilico.page.nft.model

data class CollectionTabsModel(
    val collections: List<CollectionItemModel>? = null,
    val isExpand: Boolean? = null,
)