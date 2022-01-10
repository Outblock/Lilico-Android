package io.outblock.lilico.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import io.outblock.lilico.firebase.auth.firebaseCustomLogin
import io.outblock.lilico.firebase.auth.firebaseJwt
import io.outblock.lilico.network.model.AccountKey
import io.outblock.lilico.network.model.RegisterRequest
import io.outblock.lilico.utils.clearJwtToken
import io.outblock.lilico.utils.logd
import io.outblock.lilico.wallet.getPublicKey


private const val TAG = "UserRegisterUtils"

suspend fun registerOutblockUser(
    username: String,
    callback: (isSuccess: Boolean) -> Unit,
) {
    registerOutblockUserInternal(username, callback)
}

private suspend fun registerOutblockUserInternal(
    username: String,
    callback: (isSuccess: Boolean) -> Unit,
) {
    val service = retrofit().create(ApiService::class.java)
    val user = service.register(
        RegisterRequest(
            username = username,
            accountKey = AccountKey(publicKey = getPublicKey(removePrefix = true))
        )
    )
    logd(TAG, user.toString())

    clearJwtToken()

    logd(TAG, "start delete user")
    FirebaseMessaging.getInstance().deleteToken()
    Firebase.auth.currentUser?.delete()?.addOnCompleteListener {
        logd(TAG, "delete user finish exception:${it.exception}")
        if (it.isSuccessful) {
            firebaseCustomLogin(user.data.customToken) { isSuccessful, _ ->
                if (isSuccessful) {
                    firebaseJwt { _, _ ->
                        callback(true)
                    }
                } else callback(false)
            }
        } else callback(false)
    }
}