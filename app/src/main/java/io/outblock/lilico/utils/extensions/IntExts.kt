package io.outblock.lilico.utils.extensions

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorInt
import io.outblock.lilico.base.activity.BaseActivity
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

//@ColorRes
fun Int.res2String(): String {
    return Env.getApp().getString(this)
}

@ColorInt
fun Int.res2color(context: Context? = null): Int {
    val ctx = context ?: (BaseActivity.getCurrentActivity() ?: Env.getApp())
    return ctx.getColor(this)
}

fun Float.dp2px(): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return this * scale + 0.5f
}