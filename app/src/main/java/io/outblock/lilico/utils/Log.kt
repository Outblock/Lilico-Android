package io.outblock.lilico.utils

import android.util.Log
import io.outblock.lilico.BuildConfig

fun logv(tag: String?, msg: Any?) {
    log(tag, msg, Log.VERBOSE)
}

fun logd(tag: String?, msg: Any?) {
    log(tag, msg, Log.DEBUG)
}

fun logi(tag: String?, msg: Any?) {
    log(tag, msg, Log.INFO)
}

fun logw(tag: String?, msg: Any?) {
    log(tag, msg, Log.WARN)
}

fun loge(tag: String?, msg: Any?) {
    log(tag, msg, Log.ERROR)
}

fun loge(msg: Throwable, printStackTrace: Boolean = true) {
    log("Exception", msg.message ?: "", Log.ERROR)
    if (BuildConfig.DEBUG && printStackTrace) {
        msg.printStackTrace()
    }
}

private fun log(tag: String?, msg: Any?, version: Int) {
    if (!BuildConfig.DEBUG) {
        return
    }

    val tag = "[${tag ?: ""}]"
    val msg = msg?.toString() ?: return

    when (version) {
        Log.VERBOSE -> Log.v(tag, msg)
        Log.DEBUG -> Log.d(tag, msg)
        Log.INFO -> Log.i(tag, msg)
        Log.WARN -> Log.w(tag, msg)
        Log.ERROR -> Log.e(tag, msg)
    }
}