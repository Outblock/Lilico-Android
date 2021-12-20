package io.outblock.lilico.firebase.auth

import android.text.format.DateUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.utils.*
import kotlin.math.abs

private const val TAG = "FirebaseAuth"

// TODO jwt token expire in 60 minutes
private const val JWT_TOKEN_EXPIRE_TIME = DateUtils.MINUTE_IN_MILLIS * 55

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

fun firebaseJwt(onComplete: FirebaseAuthCallback? = null) {
    val auth = Firebase.auth

    ioScope {
        if (getJwtToken().isNotBlank() && !isJwtTokenExpire()) {
            logd(TAG, "JwtToken exist!!!")
            onComplete?.invoke(true, null)
            return@ioScope
        }

        if (auth.currentUser != null) {
            fetchJwtToken(auth.currentUser, onComplete)
        } else {
            auth.signInAnonymously().addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    auth.currentUser?.let { fetchJwtToken(it, onComplete) }
                } else {
                    onComplete?.invoke(false, signInTask.exception)
                }
            }
        }
    }
}

private suspend fun isJwtTokenExpire(): Boolean = abs(System.currentTimeMillis() - getJwtRefreshTime()) >= JWT_TOKEN_EXPIRE_TIME

private fun fetchJwtToken(currentUser: FirebaseUser?, onComplete: FirebaseAuthCallback? = null) {
    currentUser ?: return
    currentUser.getIdToken(true).addOnCompleteListener { jwtTask ->
        if (jwtTask.isSuccessful) {
            val token = jwtTask.result.token.orEmpty()
            saveJwtToken(token)
            updateJwtRefreshTime()
            logd(TAG, "jwt token:$token")
            onComplete?.invoke(true, null)
        } else {
            onComplete?.invoke(false, jwtTask.exception)
        }
    }
}