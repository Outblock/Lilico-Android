package io.outblock.lilico.page.wallet.model

import io.outblock.lilico.network.model.WalletListData

data class WalletHeaderModel(
    val walletList: WalletListData,
    var balance: Float,
    var coinCount: Int = 0,
    var transactionCount: Int? = 0,
)