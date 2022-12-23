package io.outblock.lilico.network

import io.outblock.lilico.network.interceptor.HeaderInterceptor
import io.outblock.lilico.utils.isDev
import io.outblock.lilico.utils.isTesting
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


val API_HOST = if (isDev()) "https://dev.lilico.app" else "https://api.lilico.app"

fun retrofit(
    disableConverter: Boolean = false,
    network: String? = null,
): Retrofit {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(HeaderInterceptor(network = network))

        callTimeout(20, TimeUnit.SECONDS)
        connectTimeout(20, TimeUnit.SECONDS)
        readTimeout(20, TimeUnit.SECONDS)
        writeTimeout(20, TimeUnit.SECONDS)

        if (isTesting()) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()

    val builder = Retrofit.Builder()
    if (disableConverter) {
        builder.addConverterFactory(ScalarsConverterFactory.create())
    } else {
        builder.addConverterFactory(GsonConverterFactory.create())
    }
    return builder.baseUrl(API_HOST).client(client).build()
}

fun retrofitApi(): Retrofit {
    return retrofitWithHost(if (isDev()) "https://test.lilico.app" else "https://lilico.app", ignoreAuthorization = false)
}

fun retrofitWithHost(host: String, disableConverter: Boolean = false, ignoreAuthorization: Boolean = true): Retrofit {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(HeaderInterceptor(ignoreAuthorization))

        callTimeout(10, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)

        if (isTesting()) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()

    val builder = Retrofit.Builder()
    if (disableConverter) {
        builder.addConverterFactory(ScalarsConverterFactory.create())
    } else {
        builder.addConverterFactory(GsonConverterFactory.create())
    }
    return builder.baseUrl(host).client(client).build()
}