package io.outblock.lilico.page.nft.model

import io.outblock.lilico.cache.NftSelections

class NFTFragmentModel(
    val listPageData: List<Any>? = null,
    val selectionIndex: Int? = null,
    val topSelection: NftSelections? = null,
    val onListScrollChange: Int? = null,
)