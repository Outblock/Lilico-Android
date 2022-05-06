package io.outblock.lilico.page.browser.model

import android.graphics.Point
import io.outblock.lilico.page.browser.tools.BrowserTab

class BrowserModel(
    val url: String? = null,
    val searchBoxPosition: Point? = null,
    val onPageClose: Boolean? = null,
    val removeTab: BrowserTab? = null,
    val onFloatTabsHide: Boolean? = null,
    val onTabChange: Boolean? = null,
)