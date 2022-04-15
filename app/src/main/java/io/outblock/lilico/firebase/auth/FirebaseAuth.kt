package io.outblock.lilico.firebase.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FirebaseAuth"

typealias FirebaseAuthCallback = (isSuccessful: Boolean, exception: Exception?) -> Unit

fun isAnonymousSignIn(): Boolean {
    return Firebase.auth.currentUser?.isAnonymous ?: true
}

fun firebaseCustomLogin(token: String, onComplete: FirebaseAuthCallback) {
    val auth = Firebase.auth
    if (auth.currentUser != null) {
        logd(TAG, "have signed in")
        onComplete.invoke(true, null)
        return
    }
    auth.signInWithCustomToken(token).addOnCompleteListener { task ->
        onComplete.invoke(task.isSuccessful, task.exception)
    }
}

suspend fun getFirebaseJwt(forceRefresh: Boolean = false) = suspendCoroutine<String> { continuation ->
    ioScope {
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            signInAnonymously()
        }

        val user = auth.currentUser
        if (user == null) {
            continuation.resume("")
            return@ioScope
        }

        user.getIdToken(forceRefresh).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result.token.orEmpty())
            } else {
                continuation.resume("")
            }
        }
    }
}

private suspend fun signInAnonymously() = suspendCoroutine<Boolean> { continuation ->
    Firebase.auth.signInAnonymously().addOnCompleteListener { signInTask ->
        continuation.resume(signInTask.isSuccessful)
    }
}