package io.outblock.lilico.page.browser.model

import io.outblock.lilico.page.browser.BrowserParams
import io.outblock.lilico.page.browser.tools.BrowserTab

class BrowserModel(
    val url: String? = null,
    val onPageClose: Boolean? = null,
    val removeTab: BrowserTab? = null,
    val onTabChange: Boolean? = null,
    val params: BrowserParams? = null,
)