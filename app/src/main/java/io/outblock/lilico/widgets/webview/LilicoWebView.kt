package io.outblock.lilico.widgets.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

@SuppressLint("SetJavaScriptEnabled")
class LilicoWebView : WebView {

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
        }
    }
}