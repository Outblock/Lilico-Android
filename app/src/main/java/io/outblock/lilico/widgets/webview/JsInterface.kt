package io.outblock.lilico.widgets.webview

import android.webkit.JavascriptInterface
import android.webkit.WebView
import io.outblock.lilico.widgets.webview.fcl.FclMessageHandler

class JsInterface(
    private val webView: WebView,
) {

    private val messageHandler by lazy { FclMessageHandler(webView) }

    @JavascriptInterface
    fun message(data: String) {
        messageHandler.onHandleMessage(data)
    }

    companion object {
        private val TAG = JsInterface::class.java.simpleName
    }
}