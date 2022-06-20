package io.outblock.lilico.network.functions

import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.network.interceptor.HeaderInterceptor
import io.outblock.lilico.utils.*
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FirebaseFunctions"

const val FUNCTION_SIGN_AS_PAYER = "signAsPayer"

private val HOST =
    if (isDev()) "https://3ce6-2001-8003-3608-1101-8007-d82f-d449-b266.ngrok.io/lilico-dev/us-central1/" else "https://us-central1-lilico-334404.cloudfunctions.net/"


/**
 * execute firebase function
 */
suspend fun executeFunction(functionName: String, data: Any? = null): String? {
    return executeHttp(functionName, data)
}

private val functions by lazy { Firebase.functions }

private suspend fun executeHttp(functionName: String, data: Any? = null) = suspendCancellableCoroutine<String?> { continuation ->
    val client = OkHttpClient.Builder().apply {

        callTimeout(10, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)

        addInterceptor(HeaderInterceptor())

        if (isTesting()) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()
    val body = if (data == null) data else (if (data is String) data else Gson().toJson(data))

    val request = Request.Builder().url("$HOST$functionName")
        .post(body.orEmpty().toRequestBody("application/json; charset=utf-8".toMediaType()))
        .build()
    val response = client.newCall(request).execute()

    if (!response.isSuccessful) {
        logw(TAG, response.toString())
        continuation.resume(null)
        return@suspendCancellableCoroutine
    }
    continuation.resume(response.body?.string())
}

private suspend fun execute(functionName: String, data: Any? = null) = suspendCoroutine<String?> { continuation ->
    val body = if (data == null) data else (if (data is String) data else Gson().toJson(data))
    logd(TAG, "execute $functionName > body:$body")

    functions.getHttpsCallable(functionName)
        .call(body).continueWith { task ->
            if (!task.isSuccessful) {
                loge(task.exception)
                continuation.resume(null)
                return@continueWith
            }

            continuation.resume(task.result?.data?.toString())
        }
}

private fun getNetWork(): String {
    return if (isTestnet()) "testnet" else "mainnet"
}
