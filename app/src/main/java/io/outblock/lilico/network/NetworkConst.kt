package io.outblock.lilico.network

import io.outblock.lilico.BuildConfig
import io.outblock.lilico.network.interceptor.CryptoWatchHeaderInterceptor
import io.outblock.lilico.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val API_HOST = if (BuildConfig.BUILD_TYPE != "release") "https://dev.lilico.app" else "https://lilico.app"

fun retrofit(): Retrofit {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(HeaderInterceptor())

        callTimeout(10, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(API_HOST).client(client).build()
}

fun cryptoWatchRetrofit(): Retrofit {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(CryptoWatchHeaderInterceptor())

        callTimeout(10, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://api.cryptowat.ch").client(client).build()
}