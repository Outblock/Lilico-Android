package io.outblock.lilico.widgets.webview

import android.graphics.Color
import android.webkit.JavascriptInterface
import androidx.core.graphics.toColorInt
import io.outblock.lilico.R
import io.outblock.lilico.page.browser.widgets.LilicoWebView
import io.outblock.lilico.utils.extensions.res2color
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
        logd(TAG, "window color:$color")
        val colorInt = color.toColorInt()
        webView.onWindowColorChange(if (colorInt == Color.BLACK) R.color.deep_bg.res2color() else colorInt)
    }

    companion object {
        private val TAG = JsInterface::class.java.simpleName
    }
}