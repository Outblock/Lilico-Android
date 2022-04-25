package io.outblock.lilico.page.browser

import android.app.Activity
import io.outblock.lilico.widgets.floatwindow.FloatWindow

private var browserInstance: Browser? = null

internal fun getBrowser(activity: Activity, url: String? = null): Browser {
    return browserInstance ?: Browser(activity).apply {
        browserInstance = this
        url?.let { loadUrl(url) }
    }
}

fun releaseBrowser() {
    FloatWindow.dismiss(BROWSER_TAG)
    browserInstance?.onRelease()
    browserInstance = null
}

