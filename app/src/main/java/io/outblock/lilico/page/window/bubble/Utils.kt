package io.outblock.lilico.page.window.bubble

import io.outblock.lilico.utils.extensions.location

fun bubbleLocation() = bubbleInstance()?.binding()?.floatBubble?.location()

fun bubbleView() = bubbleInstance()?.binding()?.floatBubble

fun bubbleViewModel() = bubbleInstance()?.viewModel()

fun onBubbleClick() {
    bubbleViewModel()?.showFloatTabs()
}