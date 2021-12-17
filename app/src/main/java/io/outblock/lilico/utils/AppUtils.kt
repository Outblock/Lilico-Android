package io.outblock.lilico.utils

import android.app.Activity
import android.content.res.Configuration
import io.outblock.lilico.base.activity.BaseActivity

fun isNightMode(activity: Activity? = null): Boolean {
    val context = activity ?: BaseActivity.getCurrentActivity() ?: Env.getApp()
    return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}