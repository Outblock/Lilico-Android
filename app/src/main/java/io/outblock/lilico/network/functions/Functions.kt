package io.outblock.lilico.network.functions

import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.utils.loge
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val FUNCTION_SIGN_AS_PAYER = "signAsPayer"

/**
 * execute firebase function
 */
suspend fun <T> executeFunction(functionName: String, data: Any? = null): T? {
    return execute(functionName, data)
}

private val functions by lazy { Firebase.functions }

private suspend fun <T> execute(functionName: String, data: Any? = null) = suspendCoroutine<T?> { continuation ->
    functions.getHttpsCallable(functionName).call(data).continueWith { task ->
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
            continuation.resume(null)
            loge(e)
        }
    }
}