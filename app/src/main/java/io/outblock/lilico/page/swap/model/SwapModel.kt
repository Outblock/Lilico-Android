package io.outblock.lilico.page.swap.model

import io.outblock.lilico.manager.coin.FlowCoin

data class SwapModel(
    val fromCoin: FlowCoin? = null,
    val toCoin: FlowCoin? = null,
)