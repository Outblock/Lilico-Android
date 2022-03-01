package io.outblock.lilico.page.nftdetail.model

import io.outblock.lilico.network.model.Nft

class NftDetailModel(
    val nft: Nft? = null,
    val onPause: Boolean? = null,
    val onRestart: Boolean? = null,
    val onDestroy: Boolean? = null,
)