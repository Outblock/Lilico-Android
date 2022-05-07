package io.outblock.lilico.page.browser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.view.Gravity
import android.webkit.WebView
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.page.browser.tools.browserTabsCount
import io.outblock.lilico.page.browser.tools.expandWebView
import io.outblock.lilico.page.browser.tools.shrinkWebView
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.extensions.urlEncode
import io.outblock.lilico.widgets.floatwindow.FloatWindow
import io.outblock.lilico.widgets.floatwindow.FloatWindowConfig
import java.io.File
import java.net.URL

internal const val BROWSER_TAG = "Browser"

fun openBrowser(
    activity: Activity,
    url: String? = null,
    searchBoxPosition: Point? = null,
) {
    if (FloatWindow.isShowing(BROWSER_TAG)) {
        val browser = getBrowser(activity, url)
        url?.let { browser.loadUrl(it) }
        browser.open(searchBoxPosition)
        return
    }
    FloatWindow.builder().apply {
        setConfig(
            FloatWindowConfig(
                gravity = Gravity.TOP or Gravity.START,
                contentView = getBrowser(activity, url, searchBoxPosition),
                tag = BROWSER_TAG,
                isTouchEnable = true,
                disableAnimation = true,
                hardKeyEventEnable = true,
                immersionStatusBar = true,
                width = ScreenUtils.getScreenWidth(),
                height = ScreenUtils.getScreenHeight(),
                widthMatchParent = true,
                heightMatchParent = true,
                isFullScreen = true,
            )
        )
        show(activity)
    }
}

fun WebView.saveRecentRecord() {
    logd("webview", "saveRecentRecord start")
    val url = this.url.orEmpty().trim()
    val screenshot = screenshot()
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

fun WebView.screenshot(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun String.toSearchUrl(): String = "https://www.google.com/search?q=${this.trim().urlEncode()}"

fun browserViewModel() = browserInstance()?.viewModel()

fun browserViewBinding() = browserInstance()?.binding()

fun isBrowserWebViewVisible(): Boolean {
    val binding = browserViewBinding() ?: return false
    return binding.contentWrapper.isVisible() && binding.webviewContainer.childCount != 0
}

fun isBrowserActive() = FloatWindow.isShowing(BROWSER_TAG) && browserInstance() != null

fun isBrowserCollapsed() = isBrowserActive() && !isBrowserWebViewVisible()

fun onBrowserBubbleClick() {
    val binding = browserViewBinding() ?: return
    if (browserTabsCount() > 1) {
        browserViewModel()?.showFloatTabs()
        binding.floatBubble.setVisible(false, invisible = true)
    } else if (!isBrowserWebViewVisible()) {
        expandWebView(binding.contentWrapper, binding.floatBubble)
    }
}

fun shrinkBrowser() {
    val binding = browserViewBinding() ?: return
    with(binding) {
        shrinkWebView(contentWrapper, floatBubble)
    }
}

fun expandBrowser() {
    val binding = browserViewBinding() ?: return
    with(binding) {
        expandWebView(contentWrapper, floatBubble)
    }
}

fun String.toFavIcon(size: Int = 256): String {
    val url = URL(this)
    return "https://double-indigo-crab.b-cdn.net/${url.host}/$size"
}