package io.outblock.lilico.utils

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

val CACHE_PATH: File = Env.getApp().cacheDir.apply { if (!exists()) mkdirs() }

val DATA_PATH: File = Env.getApp().dataDir.apply { if (!exists()) mkdirs() }

val CACHE_VIDEO_PATH: File = File(CACHE_PATH, "video").apply { if (!exists()) mkdirs() }

fun File.toContentUri(authority: String): Uri {
    return FileProvider.getUriForFile(Env.getApp(), authority, this)
}

@WorkerThread
fun Uri?.toFile(path: String): File? {
    if (this == null) return null
    val file = File(path)
    return try {
        Env.getApp().contentResolver.openInputStream(this)?.toFile(file)
        file
    } catch (e: Exception) {
        loge(e)
        return null
    }

}

fun InputStream.toFile(file: File) {
    this.use {
        file.outputStream().use { outputStream ->
            val byte = ByteArray(2048)
            while (true) {
                val length = this.read(byte)
                if (length <= 0) {
                    break
                }
                outputStream.write(byte, 0, length)
            }
            outputStream.flush()
        }
    }
}

fun Bitmap.saveToFile(file: File, fileType: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
    file.parentFile?.let { if (!it.exists()) it.mkdirs() }
    file.outputStream().use {
        compress(fileType, 95, it)
        it.flush()
    }
}

@WorkerThread
fun String?.saveToFile(file: File) {
    if (this.isNullOrBlank()) {
        return
    }
    if (file.parentFile?.exists() != true) {
        file.parentFile?.mkdirs()
    }
    file.printWriter().use { it.write(this) }
}

@WorkerThread
fun File?.read(): String {
    if (this?.exists() != true) {
        return ""
    }
    BufferedReader(InputStreamReader(inputStream())).use { reader ->
        return reader.readText()
    }
}

fun readTextFromAssets(path: String): String? {
    try {
        BufferedReader(InputStreamReader(Env.getApp().assets.open(path))).use { reader ->
            return reader.readText()
        }
    } catch (e: Exception) {
        return null
    }
}