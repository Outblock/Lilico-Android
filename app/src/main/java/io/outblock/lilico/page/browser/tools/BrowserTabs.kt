package io.outblock.lilico.page.browser.tools

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.view.children
import io.outblock.lilico.page.browser.browserViewBinding
import io.outblock.lilico.page.browser.widgets.LilicoWebView
import io.outblock.lilico.utils.extensions.removeFromParent
import java.util.*


private val tabs = mutableListOf<BrowserTab>()

fun popBrowserTab(tabId: String) {
    val webViewContainer = webViewContainer() ?: return

    val tab = tabs.firstOrNull { it.id == tabId } ?: return
    tabs.removeAll { it.id == tabId }
    webViewContainer.removeWebView(tab.webView)
}

fun pushBrowserTab(tab: BrowserTab) {
    val webViewContainer = webViewContainer() ?: return

    tabs.remove(tab)
    tabs.add(tab)

    cleanCallbacks()

    webViewContainer.cleanWebView()
    tab.webView.removeFromParent()
    webViewContainer.addView(tab.webView)
}

fun showBrowserLastTab() {
    browserTabLast()?.let { changeBrowserTab(it.id) }
}

fun changeBrowserTab(tabId: String) {
    val tab = tabs.firstOrNull { it.id == tabId } ?: return
    pushBrowserTab(tab)
}

fun browserTabLast(): BrowserTab? = tabs.lastOrNull()

fun newBrowserTab(context: Context, url: String? = null): BrowserTab {
    return BrowserTab(
        id = UUID.randomUUID().toString(),
        webView = LilicoWebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            url?.let { loadUrl(it) }
        }
    )
}

fun newAndPushBrowserTab(url: String? = null): BrowserTab? {
    val webViewContainer = webViewContainer() ?: return null
    val tab = newBrowserTab(webViewContainer.context, url)
    pushBrowserTab(tab)
    return tab
}

fun browserTabs() = tabs.toList()

fun browserTabsCount() = tabs.size

private fun webViewContainer() = browserViewBinding()?.webviewContainer

private fun ViewGroup.cleanWebView() {
    children.forEach { if (it is WebView) removeView(it) }
}

private fun ViewGroup.removeWebView(webView: WebView) {
    children.forEach { if (it == webView) removeView(it) }
}

private fun cleanCallbacks() {
    tabs.forEach { it.webView.setWebViewCallback(null) }
}

class BrowserTab(
    val id: String,
    val webView: LilicoWebView,
) {
    fun icon() = webView.favicon

    fun title() = webView.title

    fun url() = webView.url
}