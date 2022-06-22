package io.outblock.lilico.widgets.webview

import android.graphics.Color
import android.webkit.JavascriptInterface
import io.outblock.lilico.page.browser.widgets.LilicoWebView
import io.outblock.lilico.utils.logd
import io.outblock.lilico.widgets.webview.fcl.FclMessageHandler

class JsInterface(
    private val webView: LilicoWebView,
) {

    private val messageHandler by lazy { FclMessageHandler(webView) }

    @JavascriptInterface
    fun message(data: String) {
        messageHandler.onHandleMessage(data)
    }

    @JavascriptInterface
    fun windowColor(color: String) {
        logd("xxx", "color:$color")
        webView.onWindowColorChange(Color.RED)
    }

    companion object {
        private val TAG = JsInterface::class.java.simpleName
    }
}