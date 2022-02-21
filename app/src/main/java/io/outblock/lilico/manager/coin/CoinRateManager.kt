package io.outblock.lilico.manager.coin

import android.text.format.DateUtils
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.CoinRate
import io.outblock.lilico.network.model.CoinRateQuote
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import java.util.concurrent.ConcurrentHashMap

object CoinRateManager {

    private var coinRateMap = ConcurrentHashMap<Int, CoinRate>()

    private val cache by lazy { CacheManager("COIN_RATE", CoinRateCacheData::class.java) }

    fun init() {
        ioScope {
            coinRateMap = cache.read()?.data ?: ConcurrentHashMap<Int, CoinRate>()

            val service = retrofit().create(ApiService::class.java)
            val response = service.coinRate(CoinMapManager.getCoinIdByName("Flow"))
            response.data.data.values.forEach { updateCache(it.id, it) }
        }
    }

    fun usdAmount(coinName: String = "Flow", amount: Float, asyncCallback: (amount: Float) -> Unit): Float {
        val quote = coinRateMap[CoinMapManager.getCoinIdByName(coinName)]?.usdRate()
        if (quote?.isExpire() != false) {
            ioScope {
                val service = retrofit().create(ApiService::class.java)
                val response = service.coinRate(CoinMapManager.getCoinIdByName(coinName))
                response.data.data.values.forEach {
                    updateCache(it.id, it)
                    uiScope { asyncCallback.invoke(it.usdRate()?.price!! * amount) }
                }
            }
        }

        return (quote?.price ?: -1f) * amount
    }

    fun coinRate(coinId: Int, asyncCallback: (coinRate: CoinRate) -> Unit): CoinRate? {
        val coinRate = coinRateMap[coinId]
        if (coinRate?.isExpire() != false) {
            ioScope {
                val service = retrofit().create(ApiService::class.java)
                val response = service.coinRate(coinId)
                response.data.data.values.forEach {
                    updateCache(it.id, it)
                    uiScope { asyncCallback.invoke(it) }
                }
            }
        }
        return coinRate
    }

    private fun CoinRateQuote.isExpire(): Boolean = System.currentTimeMillis() - updateTime() > 30 * DateUtils.SECOND_IN_MILLIS
    private fun CoinRate.isExpire(): Boolean = System.currentTimeMillis() - updateTime() > 30 * DateUtils.SECOND_IN_MILLIS

    private fun updateCache(coinId: Int, coinRate: CoinRate) {
        coinRateMap[coinId] = coinRate
        cache.cache(CoinRateCacheData(coinRateMap))
    }
}

private class CoinRateCacheData(
    var data: ConcurrentHashMap<Int, CoinRate>,
)