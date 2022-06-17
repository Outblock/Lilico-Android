package io.outblock.lilico.network.functions

import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logw
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FirebaseFunctions"

const val FUNCTION_SIGN_AS_PAYER = "signAsPayer"

/**
 * execute firebase function
 */
suspend fun <T> executeFunction(functionName: String, data: Any? = null): T? {
    return execute(functionName, data)
}

private val functions by lazy { Firebase.functions }

private suspend fun <T> execute(functionName: String, data: Any? = null) = suspendCoroutine<T?> { continuation ->
    val body = if (data == null) data else (if (data is String) data else Gson().toJson(data))
    logd(TAG, "execute $functionName > body:$body")

    functions.getHttpsCallable(functionName).call(body).continueWith { task ->
        if (!task.isSuccessful) {
            loge(task.exception)
            continuation.resume(null)
            return@continueWith
        }

        try {
            val result = task.result?.data?.toString()
            val obj = Gson().fromJson<T>(result, object : TypeToken<T>() {}.type)
            continuation.resume(obj)
        } catch (e: Exception) {
            if (e is FirebaseFunctionsException) {
                logw(TAG, "code:${e.code}, details:${e.details}")
            }
            loge(e)

            continuation.resume(null)
        }
    }
}