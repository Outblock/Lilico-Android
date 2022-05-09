package io.outblock.lilico.page.browser.tools

import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import io.outblock.lilico.page.window.bubble.bubbleView
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.location
import io.outblock.lilico.utils.extensions.setVisible
import kotlin.math.sqrt


fun shrinkWebView(webviewContainer: ViewGroup) {
    startAnimation(webviewContainer, false)
}

fun expandWebView(webviewContainer: ViewGroup) {
    startAnimation(webviewContainer, true)
}

private fun startAnimation(webviewContainer: ViewGroup, isExpand: Boolean) {
    if ((webviewContainer.isVisible() && isExpand) || (!webviewContainer.isVisible() && !isExpand)) {
        return
    }

    val bubbleView = bubbleView() ?: return

    TransitionManager.go(Scene(webviewContainer.parent as ViewGroup), Fade().apply { duration = 150 })

    bubbleView.post {
        val bubbleLocation = bubbleView.location()

        val centerX = bubbleLocation.x + bubbleView.width / 2
        val centerY = bubbleLocation.y + bubbleView.height / 2

        val width = webviewContainer.width
        val height = webviewContainer.height
        val radius = sqrt((width * width + height * height).toFloat()) / 2
        with(
            ViewAnimationUtils.createCircularReveal(
                webviewContainer,
                centerX,
                centerY,
                if (isExpand) 0f else radius,
                if (isExpand) radius else 0f
            )
        ) {
            duration = 250
            interpolator = AccelerateDecelerateInterpolator()
            doOnEnd { webviewContainer.setVisible(isExpand) }
            start()
            if (isExpand) {
                webviewContainer.setVisible()
            }
        }
    }
}