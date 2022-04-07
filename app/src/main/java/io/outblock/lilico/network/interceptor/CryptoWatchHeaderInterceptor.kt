package io.outblock.lilico.network.interceptor

import io.outblock.lilico.firebase.auth.firebaseJwt
import okhttp3.Interceptor
import okhttp3.Response

class CryptoWatchHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        firebaseJwt()
        val request = chain.request().newBuilder()
            .addHeader("X-CW-API-Key", "R0NY2TOWRA64477JBJIO")
            .build()
        return chain.proceed(request)
    }
}