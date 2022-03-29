package io.outblock.lilico.manager.coin

import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.CoinMapData
import io.outblock.lilico.network.model.CoinMapDataWrapper
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import java.util.concurrent.CopyOnWriteArrayList

object CoinMapManager {
    private val cache by lazy { CacheManager("COIN_MAP", CoinMapDataWrapper::class.java) }

    private val coinList = CopyOnWriteArrayList<CoinMapData>()

    fun reload(forceReload: Boolean = false) {
        ioScope {
            coinList.clear()
            coinList.addAll(cache.read()?.data.orEmpty())

            if (coinList.isNotEmpty() && !forceReload) {
                return@ioScope
            }

            runCatching {
                val service = retrofit().create(ApiService::class.java)
                val response = service.coinMap()
                if (response.status == 200 && response.data.data.isNotEmpty()) {
                    coinList.clear()
                    coinList.addAll(response.data.data)
                    cache.cache(response.data)
                }
            }
            CoinRateManager.init()
        }
    }

    fun getCoinIdBySymbol(symbol: String): Int = coinList.toList().firstOrNull { it.symbol.lowercase() == symbol.lowercase() }?.id ?: -1

    fun reloadIfEmpty() {
        if (isCoinMapEmpty()) {
            reload()
        }
    }

    private fun isCoinMapEmpty() = coinList.isEmpty()
}