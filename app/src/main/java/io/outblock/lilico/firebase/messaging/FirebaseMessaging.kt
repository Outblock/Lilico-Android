package io.outblock.lilico.firebase.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import io.outblock.lilico.R
import io.outblock.lilico.firebase.auth.isAnonymousSignIn
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofitWithHost
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.extensions.res2String
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
    sendNotification(message)
}

fun uploadPushToken() {
    ioScope {
        val token = getPushToken()
        if (token.isEmpty() || isAnonymousSignIn()) {
            return@ioScope
        }
        val retrofit = retrofitWithHost(if (isDev()) "https://scanner.lilico.app" else "scanner.lilico.app", ignoreAuthorization = false)
        val service = retrofit.create(ApiService::class.java)
        val params = mapOf(
            "token" to getPushToken(),
            "address" to WalletManager.selectedWalletAddress(),
        )
        logd(TAG, "uploadPushToken => params:$params")

        val resp = service.uploadPushToken(params)

        logd(TAG, resp)
        if (resp.status == 200) {
            updatePushToken("")
        }
    }
}

private fun sendNotification(message: RemoteMessage) {
    val requestCode = 0
    val intent = Intent(Env.getApp(), MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    intent.putExtra("data", Gson().toJson(message.data))
    val pendingIntent = PendingIntent.getActivity(
        Env.getApp(),
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE,
    )

    val channelId = Env.getApp().getString(R.string.notification_channel_id)
    val notificationBuilder = NotificationCompat.Builder(Env.getApp(), channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(message.notification?.title)
        .setContentText(message.notification?.body)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    if (message.notification?.defaultSound == true) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder.setSound(defaultSoundUri)
    }

    val notificationManager = Env.getApp().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Since android Oreo notification channel is needed.
    val channel = NotificationChannel(
        channelId,
        R.string.notification_channel_name.res2String(),
        NotificationManager.IMPORTANCE_DEFAULT,
    )
    notificationManager.createNotificationChannel(channel)

    val notificationId = 0
    notificationManager.notify(notificationId, notificationBuilder.build())
}
