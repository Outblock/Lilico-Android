package io.outblock.lilico.page.browser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.webkit.WebView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import io.outblock.lilico.R
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.page.browser.tools.browserTabLast
import io.outblock.lilico.page.browser.tools.expandWebView
import io.outblock.lilico.page.browser.tools.shrinkWebView
import io.outblock.lilico.page.window.WindowFrame
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.CACHE_PATH
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.extensions.urlEncode
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.saveToFile
import io.outblock.lilico.widgets.floatwindow.FloatWindow
import org.apache.commons.validator.routines.UrlValidator
import java.io.File
import java.net.URL

internal const val BROWSER_TAG = "Browser"

fun openBrowser(
    activity: Activity,
    url: String? = null,
    searchBoxPosition: Point? = null,
) {
    openBrowser(activity, url, BrowserParams(searchBoxPosition))
}

fun openBrowser(
    activity: Activity,
    url: String? = null,
    params: BrowserParams,
) {
    WindowFrame.browserContainer()?.setVisible()
    if (browserInstance() != null) {
        val browser = browserInstance()!!
        url?.let { browser.loadUrl(it) }
        browser.open(params)
        return
    } else {
        attachBrowser(activity, url, params)
    }
}

fun WebView.saveRecentRecord() {
    logd("webview", "saveRecentRecord start")
    val url = this.url.orEmpty().trim()
    val screenshot = screenshot() ?: return
    val title = this.title ?: return
    val icon = favicon
    logd("webview", "saveRecentRecord screenshot end")
    ioScope {
        logd("webview", "url:$url")
        logd("webview", "screenshot:$screenshot")
        screenshot.saveToFile(screenshotFile(screenshotFileName(url)))
        icon?.saveToFile(screenshotFile(faviconFileName(url)))
        AppDataBase.database().webviewRecordDao().deleteByUrl(url)
        AppDataBase.database().webviewRecordDao()
            .save(
                WebviewRecord(
                    url = url,
                    screenshot = screenshotFileName(url),
                    createTime = System.currentTimeMillis(),
                    title = title,
                    icon = if (icon == null) "" else faviconFileName(url),
                )
            )
    }
}

fun faviconFileName(url: String) = "${"webview_icon_$url".hashCode()}"

fun screenshotFileName(url: String) = "${"webview_$url".hashCode()}"

fun screenshotFile(fileName: String) = File(CACHE_PATH, fileName)

fun WebView.screenshot(): Bitmap? {
    if (width == 0 || height == 0) {
        return null
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun String.toSearchUrl(): String {
    val httpParse = if (startsWith("http://") || startsWith("https://")) this else "https://$this"
    if (UrlValidator(arrayOf("http", "https")).isValid(httpParse)) {
        return httpParse
    }

    return "https://www.google.com/search?q=${this.trim().urlEncode()}"
}

fun browserViewModel() = browserInstance()?.viewModel()

fun browserViewBinding() = browserInstance()?.binding()

fun isBrowserWebViewVisible(): Boolean {
    val binding = browserViewBinding() ?: return false
    return binding.contentWrapper.isVisible() && binding.webviewContainer.childCount != 0
}

fun isBrowserActive() = FloatWindow.isShowing(BROWSER_TAG) && browserInstance() != null

fun isBrowserCollapsed() = isBrowserActive() && !isBrowserWebViewVisible()

//fun onBrowserBubbleClick() {
//    val binding = browserViewBinding() ?: return
//    if (browserTabsCount() > 1) {
//        browserViewModel()?.showFloatTabs()
//        binding.floatBubble.setVisible(false, invisible = true)
//    } else if (!isBrowserWebViewVisible()) {
//        expandWebView(binding.contentWrapper, binding.floatBubble)
//    }
//}

fun shrinkBrowser() {
    val lastTab = browserTabLast() ?: return
    pushBubbleStack(lastTab) {
        val binding = browserViewBinding() ?: return@pushBubbleStack
        with(binding) {
            shrinkWebView(root)
        }
    }
}

fun expandBrowser() {
    val binding = browserViewBinding() ?: return
    with(binding) {
        expandWebView(root)
    }
}

fun ImageView.loadFavicon(url: String?) {
    val glideUrl = GlideUrl(url, LazyHeaders.Builder().apply {
        addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
    }.build())
    Glide.with(this).load(glideUrl).placeholder(R.drawable.placeholder_logo).into(this)
}

// https://www.google.com/s2/favicons?domain=${domain}&sz=${size}
// https://t0.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://test.find.xyz&size=256
// https://double-indigo-crab.b-cdn.net/${url.host}/$size
fun String.toFavIcon(size: Int = 256): String {
    if (this.isBlank()) {
        return this
    }
    return try {
        val url = URL(this)
        "https://double-indigo-crab.b-cdn.net/${url.host}/$size"
        // "https://www.google.com/s2/favicons?domain=${url.host}&sz=${size}"
    } catch (e: Exception) {
        this
    }
}

fun openInFlowScan(activity: Activity, transactionId: String) {
    openBrowser(activity, "https://${if (isTestnet()) "testnet." else ""}flowscan.org/transaction/$transactionId")
}

class BrowserParams(
    val searchBoxPosition: Point? = null,
    val hideToolBar: Boolean? = null,
)

