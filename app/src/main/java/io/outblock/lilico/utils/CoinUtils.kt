package io.outblock.lilico.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


fun Long.formatBalance(): Float {
    val currencyPrice = BigDecimal(9.08)
    val flowDecimal = BigDecimal(10).pow(8)
    val balance = BigDecimal(this).divide(flowDecimal)
    val currencyBalance = balance.multiply(currencyPrice)
    return balance.setScale(3, BigDecimal.ROUND_HALF_DOWN).toFloat()
}

fun Float.formatPrice(): String {
    return DecimalFormat("0.###").apply { roundingMode = RoundingMode.DOWN }.format(this)
}