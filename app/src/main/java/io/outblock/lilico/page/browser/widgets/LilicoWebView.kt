package io.outblock.lilico.page.browser.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.utils.logd
import io.outblock.lilico.widgets.webview.*

@SuppressLint("SetJavaScriptEnabled")
class LilicoWebView : WebView {
    private var callback: WebviewCallback? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr)

    init {
        with(settings) {
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            domStorageEnabled = true
        }
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        addJavascriptInterface(JsInterface(this), "android")
        setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            callback?.onScrollChange(scrollX, scrollY - oldScrollY)
        }
    }

    fun setWebViewCallback(callback: WebviewCallback) {
        this.callback = callback
    }

    private inner class WebChromeClient : com.just.agentweb.WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            logd(TAG, "newProgress:$newProgress")
            if (view.progress == newProgress) {
                callback?.onProgressChange(view.progress / 100f)
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            callback?.onTitleChange(title.orEmpty())
        }
    }

    private inner class WebViewClient : com.just.agentweb.WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            view.executeJs(JS_FCL_EXTENSIONS)
            view.executeJs(JS_LISTEN_WINDOW_FCL_MESSAGE)
            view.executeJs(JS_LISTEN_FLOW_TRANSACTION_MESSAGE)
            super.onPageFinished(view, url)
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            callback?.onPageUrlChange(url.orEmpty(), isReload)
        }
    }

    companion object {
        private val TAG = LilicoWebView::class.java.simpleName
    }
}

interface WebviewCallback {
    fun onScrollChange(scrollY: Int, offset: Int)
    fun onProgressChange(progress: Float)
    fun onTitleChange(title: String)
    fun onPageUrlChange(url: String, isReload: Boolean)
}