package io.outblock.lilico.page.browser

import android.app.Activity
import android.graphics.Point
import androidx.core.view.children
import io.outblock.lilico.page.browser.tools.clearBrowserTabs
import io.outblock.lilico.page.window.WindowFrame


internal fun browserInstance() = WindowFrame.browserContainer()?.children?.firstOrNull() as? Browser

internal fun attachBrowser(activity: Activity, url: String? = null, searchBoxPosition: Point? = null) {
    val browserContainer = WindowFrame.browserContainer() ?: return
    if (browserContainer.childCount == 0) {
        val browser = Browser(activity)
        browserContainer.addView(browser)
        with(browser) {
            url?.let { loadUrl(url) }
            open(searchBoxPosition)
        }
    }
}

fun releaseBrowser() {
    clearBrowserTabs()
    browserInstance()?.onRelease()
    WindowFrame.browserContainer()?.removeAllViews()
}

