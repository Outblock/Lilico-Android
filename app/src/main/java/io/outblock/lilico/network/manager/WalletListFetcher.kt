package io.outblock.lilico.network.manager

import androidx.annotation.WorkerThread
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import kotlinx.coroutines.delay

class WalletListFetcher(
    private val fetchCallback: (data: WalletListData) -> Unit,
) {
    private var isFetchLooperEnable = false

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
            while (isFetchLooperEnable) {
                val service = retrofit().create(ApiService::class.java)
                val resp = service.getWalletList()
                if (resp.status == 200 && !resp.data?.wallets.isNullOrEmpty()) {
                    if (isFetchLooperEnable) {
                        fetchCallback.invoke(resp.data!!)
                        walletCache().cache(resp.data)
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
}