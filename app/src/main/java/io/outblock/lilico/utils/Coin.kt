package io.outblock.lilico.utils

import io.outblock.lilico.R


enum class Coin(val value: Int) {
    FLOW(1),
    FUSD(2),
    USD(1001),
}

private val ICON_MAP by lazy {
    mapOf(
        Coin.FLOW to R.drawable.ic_coin_flow,
        Coin.FUSD to R.drawable.ic_coin_flow,
        Coin.USD to R.drawable.ic_coin_usd,
    )
}

fun Coin.icon(): Int = ICON_MAP[this] ?: 0