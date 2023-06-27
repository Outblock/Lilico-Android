package io.outblock.lilico.page.profile.subpage.wallet.childaccount.model

import io.outblock.lilico.manager.childaccount.ChildAccount

data class ChildAccountsModel(
    val accounts: List<ChildAccount>? = null,
)
