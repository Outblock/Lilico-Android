package io.outblock.lilico.cache

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.utils.*
import java.io.File

object NFTListCache {
    private val TAG = NFTListCache::class.java.simpleName

    private val file by lazy { File(CACHE_PATH, "nft_list") }

    @WorkerThread
    fun read(): NFTListData? {
        val str = file.read()
        if (str.isBlank()) {
            return null
        }

        try {
            return Gson().fromJson(str, NFTListData::class.java)
        } catch (e: Exception) {
            loge(TAG, e)
        }
        return null
    }

    fun cache(data: NFTListData) {
        ioScope {
            val str = Gson().toJson(data)
            str.saveToFile(file)
        }
    }
}