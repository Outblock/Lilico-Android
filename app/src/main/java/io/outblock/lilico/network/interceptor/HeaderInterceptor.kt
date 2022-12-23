package io.outblock.lilico.network.interceptor

import android.os.Build
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.R
import io.outblock.lilico.firebase.auth.getFirebaseJwt
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.extensions.capitalizeV2
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.logd
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

private val userAgent by lazy { "${R.string.app_name.res2String()}/${BuildConfig.VERSION_NAME} Build/${BuildConfig.VERSION_CODE} (Android ${Build.VERSION.SDK_INT}; ${deviceName()})" }

private fun deviceName(): String {
    if (Build.MODEL.lowercase().startsWith(Build.MANUFACTURER.lowercase())) {
        return Build.MODEL.capitalizeV2()
    }
    return "${Build.MANUFACTURER.capitalizeV2()} ${Build.MODEL}"
}

class HeaderInterceptor(
    private val ignoreAuthorization: Boolean = false
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (ignoreAuthorization) {
            return chain.proceed(chain.request())
        }

        val jwt = runBlocking { getFirebaseJwt() }

        logd("HeaderInterceptor", "jwt:$jwt")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $jwt")
            .addHeader("User-Agent", userAgent)
            .addHeader("Network", getNetWork())
            .build()
        return chain.proceed(request)
    }

    private fun getNetWork(): String {
        return if (isTestnet()) "testnet" else "mainnet"
    }
}