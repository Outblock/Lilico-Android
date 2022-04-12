//package io.outblock.lilico.manager.coin
//
//import android.text.format.DateUtils
//import com.google.gson.annotations.SerializedName
//import io.outblock.lilico.cache.CacheManager
//import io.outblock.lilico.network.ApiService
//import io.outblock.lilico.network.model.CoinRate
//import io.outblock.lilico.network.model.CoinRateQuote
//import io.outblock.lilico.network.retrofit
//import io.outblock.lilico.utils.ioScope
//import io.outblock.lilico.utils.logd
//import io.outblock.lilico.utils.uiScope
//import java.lang.ref.WeakReference
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.CopyOnWriteArrayList
//
//object CoinRateManager {
//    private val TAG = CoinRateManager::class.java.simpleName
//
//    private var coinRateMap = ConcurrentHashMap<String, CoinRate>()
//
//    private val listeners = CopyOnWriteArrayList<WeakReference<OnCoinRateUpdate>>()
//
//    private val cache by lazy { CacheManager("COIN_RATE", CoinRateCacheData::class.java) }
//
//    fun init() {
//        ioScope {
//            coinRateMap = ConcurrentHashMap<String, CoinRate>(cache.read()?.data.orEmpty())
//            refresh()
//        }
//    }
//
//    fun refresh() {
//        ioScope { FlowCoinListManager.getEnabledCoinList().forEach { coin -> fetchCoinRate(coin) } }
//    }
//
//    fun addListener(callback: OnCoinRateUpdate) {
//        if (listeners.firstOrNull { it.get() == callback } != null) {
//            return
//        }
//        uiScope { this.listeners.add(WeakReference(callback)) }
//    }
//
//    fun fetchCoinRate(coin: FlowCoin) {
//        CoinMapManager.reloadIfEmpty()
//        ioScope {
//            val cacheRate = coinRateMap[coin.symbol]
//            cacheRate?.let { dispatchListeners(coin, it) }
//            if (cacheRate.isExpire()) {
//                runCatching {
//                    val coinId = coin.coinId()
//                    if (coinId < 0) {
//                        return@ioScope
//                    }
//                    val service = retrofit().create(ApiService::class.java)
//                    val response = service.coinRate(coinId)
//                    response.data.data.values.forEach {
//                        updateCache(coin, it)
//                        dispatchListeners(coin, it)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun CoinRateQuote?.isExpire(): Boolean = this == null || System.currentTimeMillis() - updateTime() > 30 * DateUtils.SECOND_IN_MILLIS
//    private fun CoinRate?.isExpire(): Boolean = this == null || System.currentTimeMillis() - updateTime() > 30 * DateUtils.SECOND_IN_MILLIS
//
//    private fun updateCache(coin: FlowCoin, coinRate: CoinRate) {
//        ioScope {
//            coinRateMap[coin.symbol] = coinRate
//            cache.cache(CoinRateCacheData(coinRateMap))
//        }
//    }
//
//    private fun dispatchListeners(coin: FlowCoin, rate: CoinRate) {
//        logd(TAG, "dispatchListeners ${coin.symbol}:${rate.usdRate()?.price}")
//        uiScope {
//            listeners.removeAll { it.get() == null }
//            listeners.forEach { it.get()?.onCoinRateUpdate(coin, rate) }
//        }
//    }
//
//    private fun FlowCoin.coinId() = CoinMapManager.getCoinIdBySymbol(symbol)
//}
//
//interface OnCoinRateUpdate {
//    fun onCoinRateUpdate(coin: FlowCoin, rate: CoinRate)
//}
//
//private class CoinRateCacheData(
//    @SerializedName("data")
//    var data: Map<String, CoinRate>,
//)