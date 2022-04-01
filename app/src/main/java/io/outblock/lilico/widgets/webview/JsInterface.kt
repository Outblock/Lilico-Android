package io.outblock.lilico.widgets.webview

import android.webkit.JavascriptInterface
import android.webkit.WebView
import io.outblock.lilico.utils.logd

class JsInterface(
    private val webView: WebView,
) {

    private val messageHandler by lazy { FclMessageHandler(webView) }

    @JavascriptInterface
    fun message(data: String) {
        logd(TAG, "message:$data")
        messageHandler.onHandleMessage(data)
    }

    companion object {
        private val TAG = JsInterface::class.java.simpleName
    }
}