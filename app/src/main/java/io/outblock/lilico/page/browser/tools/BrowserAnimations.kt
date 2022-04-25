package io.outblock.lilico.page.browser.tools

import android.graphics.Point
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import kotlin.math.sqrt


fun shrinkWebView(webviewContainer: ViewGroup, bubbleView: View) {
    startAnimation(webviewContainer, bubbleView, false)
}

fun expandWebView(webviewContainer: ViewGroup, bubbleView: View) {
    startAnimation(webviewContainer, bubbleView, true)
}

private fun startAnimation(webviewContainer: ViewGroup, bubbleView: View, isExpand: Boolean) {
    if ((webviewContainer.isVisible() && isExpand) || (!webviewContainer.isVisible() && !isExpand)) {
        return
    }

    TransitionManager.go(Scene(webviewContainer.parent as ViewGroup), Fade().apply { duration = 150 })

    bubbleView.setVisible(!isExpand, invisible = true)

    val bubbleLocation = bubbleView.location()

    val centerX = bubbleLocation.x + bubbleView.width / 2
    val centerY = bubbleLocation.y + bubbleView.height / 2

    val width = webviewContainer.width
    val height = webviewContainer.height
    val radius = sqrt((width * width + height * height).toFloat()) / 2
    with(ViewAnimationUtils.createCircularReveal(webviewContainer, centerX, centerY, if (isExpand) 0f else radius, if (isExpand) radius else 0f)) {
        duration = 250
        interpolator = AccelerateDecelerateInterpolator()
        doOnEnd { webviewContainer.setVisible(isExpand) }
        start()
        if (isExpand) {
            webviewContainer.setVisible()
        }
    }
}

private fun View.location(): Point {
    val location = IntArray(2)
    getLocationInWindow(location)
    return Point(location[0], location[1])
}