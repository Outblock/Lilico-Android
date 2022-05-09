package io.outblock.lilico.page.window.bubble

import androidx.core.view.children
import io.outblock.lilico.page.window.WindowFrame
import io.outblock.lilico.page.window.bubble.tools.clearBubbleTabs


internal fun bubbleInstance() = WindowFrame.bubbleContainer()?.children?.firstOrNull() as? Bubble

fun attachBubble() {
    val bubbleContainer = WindowFrame.bubbleContainer() ?: return
    if (bubbleContainer.childCount == 0) {
        val bubble = Bubble(bubbleContainer.context)
        bubbleContainer.addView(bubble)
    }
}

fun releaseBubble() {
    clearBubbleTabs()
    WindowFrame.bubbleContainer()?.removeAllViews()
}

