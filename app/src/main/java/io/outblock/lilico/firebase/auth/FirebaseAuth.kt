package io.outblock.lilico.firebase.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.utils.logd

private const val TAG = "FirebaseAuth"

fun firebaseCustomLogin(token: String, onComplete: (isSuccessful: Boolean, exception: Exception?) -> Unit) {
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