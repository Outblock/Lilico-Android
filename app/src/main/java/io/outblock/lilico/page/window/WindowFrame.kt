package io.outblock.lilico.page.window

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.outblock.lilico.R
import io.outblock.lilico.manager.drive.GoogleDriveAuthActivity
import io.outblock.lilico.page.browser.releaseBrowser
import io.outblock.lilico.page.browser.subpage.filepicker.FilePickerActivity
import io.outblock.lilico.page.security.biometric.BiometricActivity
import io.outblock.lilico.page.security.pin.SecurityPinActivity
import io.outblock.lilico.page.window.bubble.attachBubble
import io.outblock.lilico.page.window.bubble.releaseBubble
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.widgets.floatwindow.FloatWindow
import io.outblock.lilico.widgets.floatwindow.FloatWindowConfig

class WindowFrame {

    companion object {
        private const val WINDOW_TAG = "WindowFrame"

        @SuppressLint("StaticFieldLeak")
        private var windowFrame: View? = null

        fun attach(activity: Activity) {
            if (FloatWindow.isShowing(WINDOW_TAG)) {
                return
            }

            FloatWindow.builder().apply {
                setConfig(
                    FloatWindowConfig(
                        gravity = Gravity.TOP or Gravity.START,
                        contentView = windowInstance(activity),
                        tag = WINDOW_TAG,
                        isTouchEnable = true,
                        disableAnimation = true,
                        hardKeyEventEnable = true,
                        immersionStatusBar = true,
                        width = ScreenUtils.getScreenWidth(),
                        height = ScreenUtils.getScreenHeight(),
                        widthMatchParent = true,
                        heightMatchParent = true,
                        isFullScreen = true,
                        ignorePage = listOf(
                            FilePickerActivity::class,
                            GoogleDriveAuthActivity::class,
                            BiometricActivity::class,
                            SecurityPinActivity::class,
                        )
                    )
                )
                show(activity)
            }

            attachBubble()
        }

        fun release() {
            releaseBrowser()
            releaseBubble()
            FloatWindow.dismiss(WINDOW_TAG)
        }

        fun browserContainer(): ViewGroup? {
            return windowFrame?.findViewById(R.id.browser_container)
        }

        fun bubbleContainer(): ViewGroup? {
            return windowFrame?.findViewById(R.id.bubble_container)
        }

        @SuppressLint("InflateParams")
        private fun windowInstance(activity: Activity): View {
            return windowFrame ?: LayoutInflater.from(activity).inflate(R.layout.window_frame, null).apply { windowFrame = this }
        }
    }
}