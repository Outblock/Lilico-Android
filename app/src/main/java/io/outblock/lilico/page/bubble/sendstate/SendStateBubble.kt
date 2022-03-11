package io.outblock.lilico.page.bubble.sendstate

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Point
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.manager.transaction.TransactionStateManager
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
            if (TransactionStateManager.getLastVisibleTransaction() == null) {
                logd(TAG, "no not finished transaction")
                return@ioScope
            }
            val position = getSendStateBubblePosition()
            uiScope {
                logd(TAG, "show")
                EasyFloat.dismiss(TAG, force = true)
                showFloatingWindow(activity, position)
            }
        }
    }

    fun dismiss() = EasyFloat.dismiss(TAG, force = true)

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
                        val location = view.getLocation()
                        updateSendStateBubblePosition(location)
                        uiScope { view.stateView()?.onDragEnd(location.x) }
                    }
                }
                createResult { _, _, view -> view?.stateView()?.onDragEnd(position.x, true) }
            }
            .show()
    }

    private fun View.getLocation(): Point {
        val location = intArrayOf(0, 0)
        getLocationOnScreen(location)
        val gravity = if (location[0] > ScreenUtils.getScreenWidth() / 2) Gravity.END else Gravity.START
        return Point(gravity, location[1] - statusBarHeight)
    }

    private fun View.stateView(): SendStateView? {
        return (this as? ViewGroup)?.getChildAt(0) as? SendStateView
    }
}