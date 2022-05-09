package io.outblock.lilico.page.window.bubble.tools

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
    bubbleViewModel()?.onTabChange()
    if (tabs.isEmpty()) {
        releaseBubble()
    }
}

fun bubbleTabs() = tabs.toList()

fun clearBubbleTabs() {
    tabs.clear()
}