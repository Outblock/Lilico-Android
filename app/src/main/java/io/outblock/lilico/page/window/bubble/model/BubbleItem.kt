package io.outblock.lilico.page.window.bubble.model

import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.page.browser.tools.BrowserTab

class BubbleItem(
    val data: Any,
)

fun BubbleItem.icon(): String? {
    return when (data) {
        is BrowserTab -> data.url()?.toFavIcon()
        else -> null
    }
}

fun BubbleItem.title(): String {
    return when (data) {
        is BrowserTab -> data.title().orEmpty()
        else -> ""
    }
}