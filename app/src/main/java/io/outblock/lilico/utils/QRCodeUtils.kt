package io.outblock.lilico.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

fun String.toQRBitmap(
    width: Int = 500,
    height: Int = 500,
    foregroundColor: Int = Color.BLACK,
    backgroundColor: Int = Color.WHITE,
): Bitmap? {
    val result = try {
        MultiFormatWriter().encode(this, BarcodeFormat.QR_CODE, width, height, null)
    } catch (e: IllegalArgumentException) {
        loge(e)
        return null
    }
    val w = result.width
    val h = result.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        val offset = y * w
        for (x in 0 until w) {
            pixels[offset + x] = if (result[x, y]) foregroundColor else backgroundColor
        }
    }
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
    return bitmap
}