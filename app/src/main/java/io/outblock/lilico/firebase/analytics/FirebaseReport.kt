package io.outblock.lilico.firebase.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import java.util.*

fun reportEvent(event: String, params: Map<String, String> = mapOf()) {
    ioScope {
        val bundle = Bundle()
        params.forEach { bundle.putString(it.key, it.value) }
        bundle.putString("country", Locale.getDefault().country)
        bundle.putString("language", Locale.getDefault().language)
        bundle.putString("OS", "Android")
        bundle.putString("brand", android.os.Build.BRAND)
        bundle.putString("system", android.os.Build.VERSION.RELEASE)
        FirebaseAnalytics.getInstance(Env.getApp()).logEvent(event, bundle)
        if (BuildConfig.DEBUG) {
            logd("report", "$event,params:$params")
        }
    }
}

fun reportException(event: String, ex: Throwable?, params: Map<String, String>? = null) {
    reportEvent(
        event, mutableMapOf(
            "exception" to ex?.javaClass?.simpleName.orEmpty(),
            "message" to ex?.message.orEmpty(),
        ).apply {
            params?.forEach { put(it.key, it.value) }
        }
    )
}