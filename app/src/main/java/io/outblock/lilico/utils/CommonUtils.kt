package io.outblock.lilico.utils

import io.outblock.lilico.BuildConfig


fun safeRun(printLog: Boolean = true, block: () -> Unit) {
    return try {
        block()
    } catch (e: Throwable) {
        if (printLog && BuildConfig.DEBUG) {
            loge(e)
        } else {
        }
    }
}