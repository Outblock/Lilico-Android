package io.outblock.lilico.page.webview.widgets

import android.view.ViewGroup
import android.webkit.WebView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.widgets.webview.*


class WebViewLayout(
    private val parentView: ViewGroup,
) {
    private lateinit var agentWeb: AgentWeb

    private val activity by lazy { findActivity(parentView) as FragmentActivity }

    private var callback: WebviewCallback? = null


    init {
        agentWeb = createWebView().apply {
            jsInterfaceHolder.addJavaObject("android", JsInterface(this.webCreator.webView))
        }
        agentWeb.webCreator.webView.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            callback?.onScrollChange(scrollX, scrollY - oldScrollY)
        }
    }

    fun webview() = agentWeb.webCreator.webView

    fun setWebViewCallback(callback: WebviewCallback) {
        this.callback = callback
    }

    private fun createWebView(): AgentWeb {
        val lp = CoordinatorLayout.LayoutParams(-1, -1)
        lp.behavior = ScrollingViewBehavior()
        return AgentWeb.with(activity)
            .setAgentWebParent(parentView, 0, lp)
            .closeIndicator()
            .setAgentWebWebSettings(WebViewSettings())
            .setWebChromeClient(WebChromeClient())
            .setWebViewClient(WebViewClient())
            .setWebView(NestedScrollAgentWebView(activity))
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .interceptUnkownUrl()
            .createAgentWeb()
            .ready()
            .get()
    }

    fun updateUrl(url: String) {
        agentWeb.webCreator.webView.loadUrl(url)
    }


    private inner class WebChromeClient : com.just.agentweb.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            callback?.onProgressChange(newProgress / 100f)
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

}

interface WebviewCallback {
    fun onScrollChange(scrollY: Int, offset: Int)
    fun onProgressChange(progress: Float)
    fun onTitleChange(title: String)
    fun onPageUrlChange(url: String, isReload: Boolean)
}