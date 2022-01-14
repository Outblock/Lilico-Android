package io.outblock.lilico.network.managet

import androidx.annotation.WorkerThread
import io.outblock.lilico.cache.CACHE_WALLET
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import kotlinx.coroutines.delay

class WalletListFetcher(
    private val fetchCallback: (data: WalletListData, isFromCache: Boolean) -> Unit,
) {

    private var isFetchLooperEnable = false

    private val cache by lazy { walletCache() }

    @WorkerThread
    fun cacheExist() = cache.isCacheExist()

    @WorkerThread
    fun fetch() {
        isFetchLooperEnable = true
        fetchInternal()
    }

    fun stop() {
        isFetchLooperEnable = false
    }

    private fun fetchInternal() {
        ioScope {
            fetchFromCache()
            while (isFetchLooperEnable) {
                val service = retrofit().create(ApiService::class.java)
                val resp = service.getWalletList()
                if (resp.status == 200 && !resp.data?.wallets.isNullOrEmpty()) {
                    if (isFetchLooperEnable) {
                        fetchCallback.invoke(resp.data!!, false)
                        cache.cache(resp.data)
                    }
                    isFetchLooperEnable = false
                    break
                }

                if (!isFetchLooperEnable) {
                    break
                }
                delay(2000)
            }
        }
    }

    private fun fetchFromCache() {
        if (!cache.isCacheExist()) {
            return
        }
        cache.read()?.let { fetchCallback.invoke(it, true) }
    }
}