package io.outblock.lilico.cache

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import io.outblock.lilico.utils.*
import java.io.File

class CacheManager<T>(
    private val fileName: String,
    private val type: Class<T>,
) {

    private val file by lazy { File(CACHE_PATH, fileName) }

    @WorkerThread
    fun read(): T? {
        val str = file.read()
        if (str.isBlank()) {
            return null
        }

        try {
            return Gson().fromJson(str, type)
        } catch (e: Exception) {
            loge(TAG, e)
        }
        return null
    }

    fun cache(data: T) {
        ioScope { cacheSync(data) }
    }

    fun cacheSync(data: T) {
        val str = Gson().toJson(data)
        str.saveToFile(file)
    }

    fun clear() {
        ioScope { file.delete() }
    }

    fun isCacheExist(): Boolean = file.exists() && file.length() > 0

    fun modifyTime() = file.lastModified()

    companion object {
        private val TAG = CacheManager::class.java.simpleName
    }
}