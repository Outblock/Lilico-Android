package io.outblock.lilico.manager.coin

import android.text.format.DateUtils
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.token.detail.QuoteMarket
import io.outblock.lilico.page.token.detail.getPricePair
import io.outblock.lilico.page.token.detail.isUSDStableCoin
import io.outblock.lilico.utils.getQuoteMarket
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object CoinRateManager {
    private val TAG = CoinRateManager::class.java.simpleName

    private var coinRateMap = ConcurrentHashMap<String, CoinRate>()

    private val listeners = CopyOnWriteArrayList<WeakReference<OnCoinRateUpdate>>()

    private val cache by lazy { CacheManager("COIN_RATE", CoinRateCacheData::class.java) }

    fun init() {
        ioScope {
            coinRateMap = ConcurrentHashMap<String, CoinRate>(cache.read()?.data.orEmpty())
            refresh()
        }
    }

    fun refresh() {
        ioScope { FlowCoinListManager.getEnabledCoinList().forEach { coin -> fetchCoinRate(coin) } }
    }

    fun addListener(callback: OnCoinRateUpdate) {
        if (listeners.firstOrNull { it.get() == callback } != null) {
            return
        }
        uiScope { this.listeners.add(WeakReference(callback)) }
    }

    fun coinRate(symbol: String) = coinRateMap[symbol]?.price

    fun fetchCoinRate(coin: FlowCoin) {
        ioScope {
            if (coin.isUSDStableCoin()) {
                dispatchListeners(coin, 1.0f)
                return@ioScope
            }
            val cacheRate = coinRateMap[coin.symbol]
            cacheRate?.let { dispatchListeners(coin, it.price) }
            if (cacheRate.isExpire()) {
                runCatching {
                    val market = QuoteMarket.fromMarketName(getQuoteMarket())
                    val coinPair = coin.getPricePair(market)

                    if (coinPair.isEmpty()) {
                        return@ioScope
                    }

                    val service = retrofit().create(ApiService::class.java)
                    val response = service.summary(market.value, coin.getPricePair(market))
                    val price = response.data.result.price.last
                    updateCache(coin, price)
                    dispatchListeners(coin, price)
                }
            }
        }
    }

    private fun CoinRate?.isExpire(): Boolean = this == null || System.currentTimeMillis() - updateTime > 30 * DateUtils.SECOND_IN_MILLIS

    private fun updateCache(coin: FlowCoin, price: Float) {
        ioScope {
            coinRateMap[coin.symbol] = CoinRate(coin.symbol, price, System.currentTimeMillis())
            cache.cache(CoinRateCacheData(coinRateMap))
        }
    }

    private fun dispatchListeners(coin: FlowCoin, price: Float) {
        logd(TAG, "dispatchListeners ${coin.symbol}:${price}")
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.forEach { it.get()?.onCoinRateUpdate(coin, price) }
        }
    }
}

interface OnCoinRateUpdate {
    fun onCoinRateUpdate(coin: FlowCoin, price: Float)
}

private class CoinRateCacheData(
    @SerializedName("data")
    var data: Map<String, CoinRate>,
)

class CoinRate(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("price")
    val price: Float,
    @SerializedName("updateTime")
    val updateTime: Long,
)