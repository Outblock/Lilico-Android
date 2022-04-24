package io.outblock.lilico.page.webview

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.floatview.FloatWindow
import io.outblock.lilico.widgets.floatview.FloatWindowConfig
import io.outblock.lilico.widgets.webview.LilicoWebView

object FloatWebview {

}

val webview by lazy {
    val frameLayout = FrameLayout(Env.getApp())
    LilicoWebView(Env.getApp()).apply {
        loadUrl("https://www.google.com/search?q=flow")
    }
}

fun addWebview(root: ViewGroup) {
    (webview.parent as? ViewGroup)?.removeView(webview)
    root.addView(webview, ViewGroup.LayoutParams(ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenHeight() / 2))
}

fun showWebview(activity: Activity) {
    uiScope {
        FloatWindow.dismiss("Webview")
        FloatWindow.builder().apply {
            setConfig(
                FloatWindowConfig(
                    gravity = Gravity.TOP or Gravity.START,
                    contentView = webview,
                    tag = "Webview",
                    isTouchEnable = true,
//                offsetY = ScreenUtils.getScreenHeight(),
                    disableAnimation = true,
                    hardKeyEventEnable = true,
//                immersionStatusBar = true,
                    width = ScreenUtils.getScreenWidth() / 2,
                    height = ScreenUtils.getScreenHeight() / 2,
//                widthMatchParent = true,
//                heightMatchParent = true,
                    isFullScreen = true,
                )
            )
            show()
        }
    }
}