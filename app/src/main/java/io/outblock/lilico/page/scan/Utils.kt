package io.outblock.lilico.page.scan

import android.app.Activity
import android.content.Context
import android.webkit.URLUtil
import io.outblock.lilico.page.browser.openBrowser


fun dispatchScanResult(context: Context, text: String) {
    val text = text.trim()
    if (URLUtil.isValidUrl(text)) {
        openBrowser(context as Activity, text)
    }
}