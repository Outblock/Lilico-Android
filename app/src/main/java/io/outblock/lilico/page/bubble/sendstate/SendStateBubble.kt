package io.outblock.lilico.page.bubble.sendstate

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Point
import android.view.Gravity
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.utils.*
import io.outblock.lilico.widgets.easyfloat.EasyFloat
import io.outblock.lilico.widgets.easyfloat.enums.ShowPattern
import io.outblock.lilico.widgets.easyfloat.enums.SidePattern
import io.outblock.lilico.widgets.easyfloat.utils.DisplayUtils

@SuppressLint("StaticFieldLeak")
object SendStateBubble {

    private val TAG = SendStateBubble::class.java.simpleName


    fun show(activity: Activity) {
        ioScope {
            val position = getSendStateBubblePosition()
            uiScope {
                logd(TAG, "show")
                EasyFloat.dismiss(TAG, force = true)
                showFloatingWindow(activity, position)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showFloatingWindow(activity: Activity, position: Point) {
        EasyFloat.with(activity)
            .setLayout(R.layout.window_send_state_bubble)
            .setShowPattern(ShowPattern.CURRENT_ACTIVITY)
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            .setTag(TAG)
            .setDragEnable(true)
            .setGravity(position.x, 0, position.y)
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            .registerCallback {
                dragEnd { view ->
                    cpuScope {
                        val location = intArrayOf(0, 0)
                        view.getLocationOnScreen(location)
                        val gravity = if (location[0] > ScreenUtils.getScreenWidth() / 2) Gravity.END else Gravity.START
                        updateSendStateBubblePosition(Point(gravity, location[1] - statusBarHeight))
                    }
                }
            }
            .show()
    }
}