package io.outblock.lilico.page.token.detail

import android.text.format.DateUtils
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.plusMonth
import io.outblock.lilico.utils.plusYear

enum class Period(val value: String) {
    DAY("1d"),
    WEEK("1w"),
    MONTH("1m"),
    YEAR("1y"),
    ALL("all");

    companion object {
        fun fromValue(value: String): Period {
            return values().first { it.value == value }
        }
    }
}

enum class PeriodFrequency(val value: Int) {
    fiveMinute(300),
    halfHour(1800),
    oneHour(3600),
    oneDay(86400),
    threeDay(259200),
    oneWeek(604800),
}

enum class QuoteMarket(val value: String) {
    binance("binance"),
    kraken("kraken"),
    huobi("huobi");

    companion object {

        fun fromMarketName(value: String): QuoteMarket {
            return values().first { it.value == value }
        }
    }
}

/**
[
CloseTime,
OpenPrice,
HighPrice,
LowPrice,
ClosePrice,
Volume,
QuoteVolume
]
 */
data class Quote(
    val closeTime: Long,
    val openPrice: Float,
    val highPrice: Float,
    val lowPrice: Float,
    val closePrice: Float,
    val volume: Float,
    val quoteVolume: Float,
)

fun Period.getChartPeriodTs(): Long {
    return when (this) {
        Period.DAY -> (System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS) / 1000
        Period.WEEK -> (System.currentTimeMillis() - DateUtils.WEEK_IN_MILLIS) / 1000
        Period.MONTH -> System.currentTimeMillis().plusMonth(-1) / 1000
        Period.YEAR -> System.currentTimeMillis().plusYear(-1) / 1000
        Period.ALL -> System.currentTimeMillis().plusYear(-100) / 1000
    }
}

fun Period.getChartPeriodFrequency(): Int {
    return when (this) {
        Period.DAY -> PeriodFrequency.halfHour.value
        Period.WEEK -> PeriodFrequency.oneHour.value
        Period.MONTH -> PeriodFrequency.oneDay.value
        Period.YEAR -> PeriodFrequency.threeDay.value
        Period.ALL -> PeriodFrequency.oneWeek.value
    }
}

fun QuoteMarket.getUSDCPricePair(): String {
    return when (this) {
        QuoteMarket.kraken -> "usdcusd"
        else -> "usdcusdt"
    }
}

fun QuoteMarket.getFlowPricePair(): String {
    return when (this) {
        QuoteMarket.kraken -> "flowusd"
        else -> "flowusdt"
    }
}

fun FlowCoin.getPricePair(market: QuoteMarket): String {
    return when (symbol) {
        FlowCoin.SYMBOL_FLOW, FlowCoin.SYMBOL_STFLOW -> market.getFlowPricePair()
        FlowCoin.SYMBOL_FUSD, FlowCoin.SYMBOL_USDC -> market.getUSDCPricePair()
        else -> ""
    }
}

fun FlowCoin.isUSDStableCoin(): Boolean {
    return symbol == FlowCoin.SYMBOL_FUSD || symbol == FlowCoin.SYMBOL_USDC
}

@Suppress("UNCHECKED_CAST")
fun Map<String, Any>.parseMarketQuoteData(period: Period): List<Quote> {
    try {
        val array =
            (((this["data"] as Map<String, *>)["result"]) as? Map<String, *>)?.get("${period.getChartPeriodFrequency()}") as? List<List<Number>>
                ?: return emptyList()
        return array.map { line ->
            Quote(
                closeTime = line[0].toLong(),
                openPrice = line[1].toFloat(),
                highPrice = line[2].toFloat(),
                lowPrice = line[3].toFloat(),
                closePrice = line[4].toFloat(),
                volume = line[5].toFloat(),
                quoteVolume = line[6].toFloat(),
            )
        }
    } catch (e: Exception) {
        loge(e)
    }
    return emptyList()
}