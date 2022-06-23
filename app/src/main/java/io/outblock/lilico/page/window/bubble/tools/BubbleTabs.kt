package io.outblock.lilico.page.window.bubble.tools

import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.browser.tools.popBrowserTab
import io.outblock.lilico.page.window.bubble.attachBubble
import io.outblock.lilico.page.window.bubble.bubbleViewModel
import io.outblock.lilico.page.window.bubble.model.BubbleItem
import io.outblock.lilico.page.window.bubble.releaseBubble
import io.outblock.lilico.utils.uiScope

private val tabs = mutableListOf<BubbleItem>()

fun pushBubbleStack(data: Any, onPushed: (() -> Unit)? = null) {
    uiScope {
        tabs.removeAll { it.data == data }
        tabs.add(BubbleItem(data))
        attachBubble()
        bubbleViewModel()?.onTabChange()
        onPushed?.invoke()
    }
}

fun popBubbleStack(data: Any) {
    uiScope {
        tabs.removeAll { it.data == data }
        if (data is BrowserTab) {
            popBrowserTab(data.id)
        }
        bubbleViewModel()?.onTabChange()
        if (tabs.isEmpty()) {
            releaseBubble()
        }
    }
}

fun updateBubbleStack(data: Any) {
    uiScope { bubbleViewModel()?.onTabChange() }
}

fun inBubbleStack(data: Any): Boolean = tabs.toList().firstOrNull { it.data == data } != null

fun bubbleTabs() = tabs.toList()

fun bubbleTabCount() = tabs.size

fun clearBubbleTabs() {
    tabs.toList().forEach {
        popBubbleStack(it.data)
    }
}