package io.outblock.lilico.page.scan

import android.app.Activity
import android.content.Context
import android.webkit.URLUtil
import io.outblock.lilico.manager.config.AppConfig
import io.outblock.lilico.manager.walletconnect.WalletConnect
import io.outblock.lilico.page.browser.openBrowser


fun dispatchScanResult(context: Context, str: String) {
    val text = str.trim()
    if (text.isBlank()) {
        return
    }

    if (text.startsWith("wc:") && AppConfig.walletConnectEnable()) {
        WalletConnect.get().pair(text)
    } else if (URLUtil.isValidUrl(text.httpPrefix())) {
        openBrowser(context as Activity, text.httpPrefix())
    }
}

private fun String.httpPrefix(): String {
    if (startsWith("http")) return this

    return "https://$this"
}