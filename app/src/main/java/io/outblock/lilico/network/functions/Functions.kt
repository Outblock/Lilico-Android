package io.outblock.lilico.network.functions

import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.firebase.auth.getFirebaseJwt
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.isDev
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logw
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FirebaseFunctions"

const val FUNCTION_SIGN_AS_PAYER = "signAsPayer"

private val HOST =
    if (isDev()) "https://3ce6-2001-8003-3608-1101-8007-d82f-d449-b266.ngrok.io/lilico-dev/us-central1/" else "https://us-central1-lilico-334404.cloudfunctions.net/"


/**
 * execute firebase function
 */
suspend fun <T> executeFunction(functionName: String, data: Any? = null): T? {
    return executeHttp(functionName, data)
}

private val functions by lazy { Firebase.functions }

private suspend fun <T> executeHttp(functionName: String, data: Any? = null) = suspendCancellableCoroutine<T?> { continuation ->
    val client = OkHttpClient()
    val body = if (data == null) data else (if (data is String) data else Gson().toJson(data))
    logd(TAG, "$functionName http body:$body")

    val jwt = runBlocking { getFirebaseJwt() }

    val request = Request.Builder().url("$HOST$functionName")
        .post(body.orEmpty().toRequestBody("application/json; charset=utf-8".toMediaType()))
        .addHeader("authorization", "Bearer $jwt")
        .addHeader("Network", getNetWork())
        .build()
    val response = client.newCall(request).execute()

    if (response.isSuccessful) {
        logw(TAG, response.toString())
        continuation.resume(null)
        return@suspendCancellableCoroutine
    }
    continuation.resume(parseResponse(response.body?.string()))
}

private suspend fun <T> execute(functionName: String, data: Any? = null) = suspendCoroutine<T?> { continuation ->
    val body = if (data == null) data else (if (data is String) data else Gson().toJson(data))
    logd(TAG, "execute $functionName > body:$body")

    functions.getHttpsCallable(functionName)
        .call(body).continueWith { task ->
            if (!task.isSuccessful) {
                loge(task.exception)
                continuation.resume(null)
                return@continueWith
            }

            continuation.resume(parseResponse(task.result?.data?.toString()))
        }
}

private fun <T> parseResponse(data: String?): T? {
    return try {
        logd(TAG, "response:$data")
        return Gson().fromJson<T>(data, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

private fun getNetWork(): String {
    return if (isTestnet()) "testnet" else "mainnet"
}
