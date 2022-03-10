package io.outblock.lilico.page.bubble.sendstate

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Point
import android.view.Gravity
import io.outblock.lilico.R
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.cpuScope
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.easyfloat.EasyFloat
import io.outblock.lilico.widgets.easyfloat.enums.ShowPattern
import io.outblock.lilico.widgets.easyfloat.enums.SidePattern
import io.outblock.lilico.widgets.easyfloat.utils.DisplayUtils

@SuppressLint("StaticFieldLeak")
object SendStateBubble {

    private val TAG = SendStateBubble::class.java.simpleName

    private var position: Point = Point(0, (ScreenUtils.getScreenHeight() * 0.3f).toInt())

    fun show(activity: Activity) {
        ioScope {
            uiScope { showFloatingWindow(activity, position) }
        }
    }

    @SuppressLint("InflateParams")
    private fun showFloatingWindow(activity: Activity, bubblePosition: Point) {
        EasyFloat.with(activity)
            .setLayout(R.layout.window_send_state_bubble)
            .setShowPattern(ShowPattern.CURRENT_ACTIVITY)
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            .setTag(TAG)
            .setDragEnable(true)
            .setLocation(bubblePosition.x, bubblePosition.y)
            .setGravity(Gravity.END, 0, 10)
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            .registerCallback {
                dragEnd { view ->
                    cpuScope {
                    }
                }
            }
            .show()
    }
}