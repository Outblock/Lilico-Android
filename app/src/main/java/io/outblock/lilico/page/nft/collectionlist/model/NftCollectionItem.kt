package io.outblock.lilico.page.nft.collectionlist.model

import io.outblock.lilico.manager.config.NftCollection

data class NftCollectionItem(
    val collection: NftCollection,
    var isAdded: Boolean,
    var isAdding: Boolean? = null,
) {
    fun isNormalState() = !(isAdded || isAdding == true)
}