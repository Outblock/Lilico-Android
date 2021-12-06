package io.outblock.lilico.utils.extensions

import android.content.res.Resources
import io.outblock.lilico.utils.Env


//@ColorRes
fun Int.res2pix(): Int {
    return Env.getApp().resources.getDimensionPixelSize(this)
}

fun Int.res2dip(): Float {
    return Env.getApp().resources.getDimension(this) / Env.getApp().resources.displayMetrics.density
}

fun Int.dp2px(): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return this * scale + 0.5f
}

fun Float.dp2px(): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return this * scale + 0.5f
}