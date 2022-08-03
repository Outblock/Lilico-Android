package io.outblock.lilico.page.inbox.model

import io.outblock.lilico.network.model.InboxNft
import io.outblock.lilico.network.model.InboxToken

class InboxPageModel(
    val tokenList: List<InboxToken>? = null,
    val nftList: List<InboxNft>? = null,
    val claimExecuting: Boolean? = null,
)