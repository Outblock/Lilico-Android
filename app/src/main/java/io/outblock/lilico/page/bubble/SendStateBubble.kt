package io.outblock.lilico.page.bubble

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.easyfloat.EasyFloat
import io.outblock.lilico.widgets.easyfloat.enums.ShowPattern
import io.outblock.lilico.widgets.easyfloat.enums.SidePattern
import io.outblock.lilico.widgets.easyfloat.utils.DisplayUtils

@SuppressLint("StaticFieldLeak")
object SendStateBubble {

    private val TAG = SendStateBubble::class.java.simpleName

    private var position: Point = Point(0, (ScreenUtils.getScreenHeight() * 0.3f).toInt())

    fun show() {
        ioScope {
            uiScope { showFloatingWindow(position) }
        }
    }

    @SuppressLint("InflateParams")
    private fun showFloatingWindow(bubblePosition: Point) {
        EasyFloat.with(Env.getApp())
            // 设置浮窗xml布局文件，并可设置详细信息
            .setLayout(R.layout.window_send_state_bubble)
            // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示
            .setShowPattern(ShowPattern.ALL_TIME)
            // 设置吸附方式，共15种模式，详情参考SidePattern
            .setSidePattern(SidePattern.DEFAULT)
            // 设置浮窗的标签，用于区分多个浮窗
            .setTag(TAG)
            // 设置浮窗是否可拖拽
            .setDragEnable(true)
            // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
            .setLocation(bubblePosition.x, bubblePosition.y)
            .setHardKeyEventEnable(true)
            // 设置浮窗的对齐方式和坐标偏移量
            .setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, 0)
            // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
            .setMatchParent(widthMatch = true, heightMatch = false)
            // 设置浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
//            .setAnimator(ScaleAlphaAnimator())
            // 设置系统浮窗的不需要显示的页面
//            .setFilter(MainActivity::class.java)
            // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
//            .setWindowHeight(height)
//            .setIgnoreDragViewList(listOf(R.id.window_style_view, R.id.lyric_line_view, R.id.expand_button))
//            .setForceDragViewList(listOf(R.id.close_button))
            // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
            // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
            .registerCallback {
                dragEnd { view ->
                    cpuScope {
                    }
                }
            }
            .show()
    }
}