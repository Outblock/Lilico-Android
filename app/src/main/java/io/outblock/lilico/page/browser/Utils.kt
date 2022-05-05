package io.outblock.lilico.page.browser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.view.Gravity
import android.webkit.WebView
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.WebviewRecord
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.urlEncode
import io.outblock.lilico.widgets.floatwindow.FloatWindow
import io.outblock.lilico.widgets.floatwindow.FloatWindowConfig
import java.io.File

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