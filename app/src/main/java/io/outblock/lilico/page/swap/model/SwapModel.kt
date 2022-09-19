package io.outblock.lilico.page.swap.model

import io.outblock.lilico.manager.coin.FlowCoin

data class SwapModel(
    val fromCoin: FlowCoin? = null,
    val toCoin: FlowCoin? = null,
    val onBalanceUpdate: Boolean? = null,
    val onCoinRateUpdate: Boolean? = null,
    val onEstimateFromUpdate: Float? = null,
    val onEstimateToUpdate: Float? = null,
    val onEstimateLoading: Boolean? = null,
)