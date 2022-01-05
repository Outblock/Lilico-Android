package io.outblock.lilico.network.interceptor

import io.outblock.lilico.firebase.auth.firebaseJwt
import io.outblock.lilico.utils.getJwtToken
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        firebaseJwt()
        val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${getJwtToken()}").build()
        return chain.proceed(request)
    }
}