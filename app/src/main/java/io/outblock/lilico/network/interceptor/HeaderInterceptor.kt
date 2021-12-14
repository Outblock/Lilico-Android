package io.outblock.lilico.network.interceptor

import io.outblock.lilico.utils.getJwtToken
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder().addQueryParameter("jwt", getJwtToken()).build()
        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}