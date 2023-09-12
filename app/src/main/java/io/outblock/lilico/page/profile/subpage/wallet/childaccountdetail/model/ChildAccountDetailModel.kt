package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.model

import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.CoinData
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.CollectionData

class ChildAccountDetailModel(
    val account: ChildAccount? = null,
    val nftCollections: List<CollectionData>? = null,
    val coinList: List<CoinData>? = null,
)