package io.outblock.lilico.manager.account

import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList


object WalletManager {
    private val TAG = WalletManager::class.java.simpleName

    private val listeners = CopyOnWriteArrayList<WeakReference<OnWalletDataUpdate>>()

    private var wallet: WalletListData? = null

    private val apiService by lazy { retrofit().create(ApiService::class.java) }
    private val cache by lazy { CacheManager("WALLET_CACHE", WalletListData::class.java) }

    fun init() {
        ioScope { wallet = cache.read() }
    }

    suspend fun fetch(useCache: Boolean = true) {
        if (useCache) {
            wallet?.let { dispatchListeners(it) }
        }
        while (true) {
            val resp = apiService.getWalletList()

            // request success & wallet list is empty (wallet not create finish)
            if (resp.status == 200 && !resp.data?.wallets.isNullOrEmpty()) {
                dispatchListeners(resp.data!!)
                walletCache().cache(resp.data)
                break
            }

            delay(2000)
        }
    }

    fun addListener(callback: OnWalletDataUpdate) {
        uiScope { this.listeners.add(WeakReference(callback)) }
    }

    private fun dispatchListeners(wallet: WalletListData) {
        logd(TAG, "dispatchListeners:$wallet")
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.forEach { it.get()?.onWalletDataUpdate(wallet) }
        }
    }
}

interface OnWalletDataUpdate {
    fun onWalletDataUpdate(wallet: WalletListData)
}