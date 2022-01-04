package io.outblock.lilico.network.managet

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.*
import kotlinx.coroutines.delay
import java.io.File

class WalletListFetcher(
    private val fetchCallback: (data: WalletListData, isFromCache: Boolean) -> Unit,
) {

    private var isFetchLooperEnable = false

    fun cacheExist() = cacheFile().exists()

    @WorkerThread
    fun fetch() {
        isFetchLooperEnable = true
        fetchInternal()
    }

    fun stop() {
        isFetchLooperEnable = false
    }

    private fun cacheFile() = File(DATA_PATH, "${"wallet".hashCode()}")

    private fun fetchInternal() {
        ioScope {
            fetchFromCache()
            while (isFetchLooperEnable) {
                val service = retrofit().create(ApiService::class.java)
                val resp = service.getWalletList()
                if (resp.status == 200 && !resp.data?.wallets.isNullOrEmpty()) {
                    if (isFetchLooperEnable) {
                        fetchCallback.invoke(resp.data!!, false)
                        Gson().toJson(resp.data).saveToFile(cacheFile())
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
        if (!cacheFile().exists()) {
            return
        }
        val dataStr = cacheFile().read()
        safeRun {
            val data = Gson().fromJson(dataStr, WalletListData::class.java)
            fetchCallback.invoke(data, true)
        }
    }

}