package io.outblock.lilico.utils

import android.util.Log
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.firebase.analytics.reportException

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

fun loge(msg: Throwable?, printStackTrace: Boolean = true) {
    log("Exception", msg?.message ?: "", Log.ERROR)
    if (printLog() && printStackTrace) {
        msg?.printStackTrace()
    }
    ioScope { msg?.let { reportException("exception_report", it) } }
}

private fun log(tag: String?, msg: Any?, version: Int) {
    if (!printLog()) {
        return
    }

    val tag = "[${tag ?: ""}]"
    val text = msg?.toString() ?: return
    val maxLength = 3500
    if (text.length > maxLength) {
        val chunkCount: Int = text.length / maxLength // integer division
        for (i in 0..chunkCount) {
            val max = maxLength * (i + 1)
            if (max >= text.length) {
                print(tag, text.substring(maxLength * i), version)
            } else {
                print(tag, text.substring(maxLength * i, max), version)
            }
        }
    } else print(tag, text, version)
}

private fun print(tag: String, msg: String, version: Int) {
    when (version) {
        Log.VERBOSE -> Log.v(tag, msg)
        Log.DEBUG -> Log.d(tag, msg)
        Log.INFO -> Log.i(tag, msg)
        Log.WARN -> Log.w(tag, msg)
        Log.ERROR -> Log.e(tag, msg)
    }
}

private fun printLog() = BuildConfig.DEBUG || isDev()
//private fun printLog() = true