package io.outblock.lilico.page.browser

import android.app.Activity
import android.graphics.Point
import io.outblock.lilico.page.browser.tools.clearBrowserTabs
import io.outblock.lilico.widgets.floatwindow.FloatWindow

private var browserInstance: Browser? = null

internal fun getBrowser(activity: Activity, url: String? = null, searchBoxPosition: Point? = null): Browser {
    return browserInstance ?: Browser(activity).apply {
        browserInstance = this
        url?.let { loadUrl(url) }
        open(searchBoxPosition)
    }
}

internal fun browserInstance() = browserInstance

fun releaseBrowser() {
    clearBrowserTabs()
    FloatWindow.dismiss(BROWSER_TAG)
    browserInstance?.onRelease()
    browserInstance = null
}

