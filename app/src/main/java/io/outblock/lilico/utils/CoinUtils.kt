package io.outblock.lilico.utils

import java.math.BigDecimal


fun Long.formatBalance(): Float {
    val currencyPrice = BigDecimal(9.08)
    val flowDecimal = BigDecimal(10).pow(8)
    val balance = BigDecimal(this).divide(flowDecimal)
    val currencyBalance = balance.multiply(currencyPrice)
    return balance.setScale(3).toFloat()
}