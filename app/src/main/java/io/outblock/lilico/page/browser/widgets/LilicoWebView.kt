package io.outblock.lilico.page.browser.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.annotation.ColorInt
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.page.browser.subpage.filepicker.showWebviewFilePicker
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
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
//            userAgentString = USER_AGENT
        }
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        addJavascriptInterface(JsInterface(this), "android")
        setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            callback?.onScrollChange(scrollX, scrollY - oldScrollY)
        }

        with(CookieManager.getInstance()) {
            setAcceptThirdPartyCookies(this@LilicoWebView, true)
            acceptCookie()
            setAcceptCookie(true)
        }
    }

    fun setWebViewCallback(callback: WebviewCallback?) {
        this.callback = callback
    }

    fun onWindowColorChange(@ColorInt color: Int) {
        callback?.onWindowColorChange(color)
    }

    private inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (view.progress == newProgress) {
                callback?.onProgressChange(view.progress / 100f)
            }

            if (newProgress == 100) {
                logd(TAG, "load finish")
                view.executeJs(JS_QUERY_WINDOW_COLOR)
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            callback?.onTitleChange(title.orEmpty())
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            uiScope { showWebviewFilePicker(context, filePathCallback, fileChooserParams) }
            return true
        }
    }

    private inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            view.executeJs(JS_FCL_EXTENSIONS)
            view.executeJs(JS_LISTEN_WINDOW_FCL_MESSAGE)
            view.executeJs(JS_LISTEN_FLOW_WALLET_TRANSACTION)
            super.onPageFinished(view, url)
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            callback?.onPageUrlChange(url.orEmpty(), isReload)
        }
    }

    companion object {
        private val TAG = LilicoWebView::class.java.simpleName
        const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36"
    }
}

interface WebviewCallback {
    fun onScrollChange(scrollY: Int, offset: Int)
    fun onProgressChange(progress: Float)
    fun onTitleChange(title: String)
    fun onPageUrlChange(url: String, isReload: Boolean)
    fun onWindowColorChange(@ColorInt color: Int)
}