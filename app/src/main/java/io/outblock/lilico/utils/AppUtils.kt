package io.outblock.lilico.utils

import android.app.Activity
import android.content.res.Configuration
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.firebase.storage.firebaseImage

fun isNightMode(activity: Activity? = null): Boolean {
    val context = activity ?: BaseActivity.getCurrentActivity() ?: Env.getApp()
    return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun String?.parseAvatarUrl(): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    val url = this.firebaseImage()
    return url
}

fun isDev() = BuildConfig.APPLICATION_ID.contains("dev")

fun isTesting() = BuildConfig.DEBUG