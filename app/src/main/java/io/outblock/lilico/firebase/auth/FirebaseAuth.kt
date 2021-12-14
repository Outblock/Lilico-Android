package io.outblock.lilico.firebase.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.utils.getJwtToken
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.saveJwtToken

private const val TAG = "FirebaseAuth"

typealias FirebaseAuthCallback = (isSuccessful: Boolean, exception: Exception?) -> Unit

fun firebaseCustomLogin(token: String, onComplete: FirebaseAuthCallback) {
    val auth = Firebase.auth
    if (auth.currentUser != null) {
        logd(TAG, "已登陆")
        onComplete.invoke(true, null)
        return
    }
    auth.signInWithCustomToken(token).addOnCompleteListener { task ->
        onComplete.invoke(task.isSuccessful, task.exception)
    }
}

fun firebaseJwt(onComplete: FirebaseAuthCallback? = null) {
    val auth = Firebase.auth

    if (getJwtToken().isNotBlank()) {
        return
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

private fun fetchJwtToken(currentUser: FirebaseUser?, onComplete: FirebaseAuthCallback? = null) {
    currentUser ?: return
    currentUser.getIdToken(true).addOnCompleteListener { jwtTask ->
        if (jwtTask.isSuccessful) {
            val token = jwtTask.result.token.orEmpty()
            saveJwtToken(token)
            logd(TAG, "jwt token:$token")
            onComplete?.invoke(true, null)
        } else {
            onComplete?.invoke(false, jwtTask.exception)
        }
    }
}