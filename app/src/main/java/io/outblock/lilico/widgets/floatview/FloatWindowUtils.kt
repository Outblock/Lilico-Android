package io.outblock.lilico.widgets.floatview

import android.graphics.PixelFormat
import android.view.WindowManager
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.widgets.easyfloat.utils.LifecycleUtils

internal fun createParams(config: FloatWindowConfig): WindowManager.LayoutParams {
    val focusableFlag = if (config.hardKeyEventEnable) 0 else WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    return WindowManager.LayoutParams().apply {
        // 安卓6.0 以后，全局的Window类别，必须使用TYPE_APPLICATION_OVERLAY
//        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//        else WindowManager.LayoutParams.TYPE_PHONE
        type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL

        format = PixelFormat.TRANSLUCENT
        gravity = config.gravity
        // 设置浮窗以外的触摸事件可以传递给后面的窗口、不自动获取焦点
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or focusableFlag
        if (config.immersionStatusBar) {
            // 没有边界限制，允许窗口扩展到屏幕外
            flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        }

        if (config.isFullScreen) {
            flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
              WindowManager.LayoutParams.FLAG_FULLSCREEN or
              WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        if (!config.isTouchEnable) {
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }

        // 防止悬浮窗遮挡到键盘
        flags = flags or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM

        width = when {
            config.width != 0 -> config.width
            config.widthMatchParent -> WindowManager.LayoutParams.MATCH_PARENT
            else -> WindowManager.LayoutParams.WRAP_CONTENT
        }
        height = when {
            config.height != 0 -> config.height
            config.heightMatchParent -> WindowManager.LayoutParams.MATCH_PARENT
            config.immersionStatusBar && config.heightMatchParent -> ScreenUtils.getScreenHeight()
            else -> WindowManager.LayoutParams.WRAP_CONTENT
        }
        x = config.offsetX
        y = config.offsetY

        token = LifecycleUtils.getTopActivity()?.window?.decorView?.windowToken

        if (config.disableAnimation) {
            try {
                this.javaClass.getField("privateFlags")
                    .set(this, this.javaClass.getField("privateFlags").get(this) as Int or 0x00000040)
            } catch (e: Exception) {
            }
        }
    }

}