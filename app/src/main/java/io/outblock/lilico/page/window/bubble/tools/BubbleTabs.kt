package io.outblock.lilico.page.window.bubble.tools

import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.browser.tools.popBrowserTab
import io.outblock.lilico.page.window.bubble.attachBubble
import io.outblock.lilico.page.window.bubble.bubbleViewModel
import io.outblock.lilico.page.window.bubble.model.BubbleItem
import io.outblock.lilico.page.window.bubble.releaseBubble

private val tabs = mutableListOf<BubbleItem>()

fun pushBubbleStack(data: Any) {
    tabs.removeAll { it.data == data }
    tabs.add(BubbleItem(data))
    attachBubble()
    bubbleViewModel()?.onTabChange()
}

fun popBubbleStack(data: Any) {
    tabs.removeAll { it.data == data }
    if (data is BrowserTab) {
        popBrowserTab(data.id)
    }
    bubbleViewModel()?.onTabChange()
    if (tabs.isEmpty()) {
        releaseBubble()
    }
}

fun inBubbleStack(data: Any): Boolean = tabs.toList().firstOrNull { it.data == data } != null

fun bubbleTabs() = tabs.toList()

fun bubbleTabCount() = tabs.size

fun clearBubbleTabs() {
    tabs.toList().forEach {
        popBubbleStack(it.data)
    }
}