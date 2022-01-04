package io.outblock.lilico.page.wallet.model

import wallet.core.jni.CoinType

data class WalletCoinItemModel(
    val type: CoinType,
    val address: String,
    val balance: Long,
)