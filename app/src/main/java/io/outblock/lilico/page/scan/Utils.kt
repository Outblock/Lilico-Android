package io.outblock.lilico.page.scan

import android.content.Context
import android.webkit.URLUtil
import io.outblock.lilico.page.browser.WebViewActivity


fun dispatchScanResult(context: Context, text: String) {
    val text = text.trim()
    if (URLUtil.isValidUrl(text)) {
        WebViewActivity.open(context, text)
    }
}