package io.outblock.lilico.firebase.messaging

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.firebase.auth.isAnonymousSignIn
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofitWithHost
import io.outblock.lilico.utils.getPushToken
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isDev
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logw
import io.outblock.lilico.utils.updatePushToken

private const val TAG = "FirebaseMessaging"

fun getFirebaseMessagingToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            loge(task.exception)
            return@addOnCompleteListener
        }
        val token = task.result
        logd(TAG, "token:$token")
        updatePushToken(token)
        uploadPushToken()
    }
}

fun subscribeMessagingTopic(topic: String) {
    logd(TAG, "subscribeMessagingTopic => $topic")
    Firebase.messaging.subscribeToTopic(topic)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                logw(TAG, "msg subscribe failed")
                return@addOnCompleteListener
            }
            logd(TAG, "firebase message subscribe success")
        }
}

fun parseFirebaseMessaging(message: RemoteMessage) {
    val data = message.data
    val body = message.notification?.body
    val title = message.notification?.title

    logd(TAG, "parseFirebaseMessaging => data:$data,title:$title,body:$body")
}

fun uploadPushToken() {
    ioScope {
        val token = getPushToken()
        val userId = userInfoCache().read()?.username ?: return@ioScope
        if (token.isEmpty() || isAnonymousSignIn()) {
            return@ioScope
        }
        val retrofit = retrofitWithHost(if (isDev()) "https://scanner.lilico.app" else "scanner.lilico.app", ignoreAuthorization = false)
        val service = retrofit.create(ApiService::class.java)
        val params = mapOf(
            "token" to getPushToken(),
            "id" to userId,
            "address" to WalletManager.selectedWalletAddress(),
        )
        logd(TAG, "uploadPushToken => params:$params")

        val resp = service.uploadPushToken(params)

        if (resp.status == 200) {
            updatePushToken("")
        }
        logd(TAG, resp)
    }
}