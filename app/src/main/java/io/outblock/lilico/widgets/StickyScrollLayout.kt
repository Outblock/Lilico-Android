package io.outblock.lilico.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import com.donkingliang.consecutivescroller.ConsecutiveScrollerLayout

class StickyScrollLayout : FrameLayout {

    private val scrollView by lazy { getChildAt(0) as ConsecutiveScrollerLayout }

    private lateinit var stickyView: View
    private lateinit var stickyParent: ViewGroup
    private var stickyPosition = -1

    private val stickyHolder by lazy { FrameLayout(context) }

    private var offsetY = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        post {
//            assert(children.firstOrNull { it is ConsecutiveScrollerLayout } != null)
//            scrollView.onVerticalScrollChangeListener  = ConsecutiveScrollerLayout.OnScrollChangeListener { _, scrollY, _, _ -> onScrollChange(scrollY) }
//            addView(stickyHolder)
        }
    }

    fun setStickyView(@IdRes stickyId: Int) {
        stickyView = findViewById(stickyId)
        stickyParent = stickyView.parent as ViewGroup
        stickyPosition = -1
    }

    fun setOffsetY(offset: Int) {
        this.offsetY = offset
        stickyHolder.setPadding(0, offsetY - 1, 0, 0)
    }

    private fun onScrollChange(scrollY: Int) {
        post {
            if (!this::stickyView.isInitialized) {
                return@post
            }

//            logd("xxx","stickyPosition:$stickyPosition, stickyParent.top:${stickyParent.top}, scrollY:$scrollY, offsetY:$offsetY")
//            logd("xxx","stickyView:$stickyView, stickyParent:${stickyParent}, stickyView.parent:${stickyView.parent}, stickyHolder:${stickyHolder}")
            if (stickyPosition < 0 && stickyView.parent == stickyParent && stickyParent.top != 0) {
                stickyPosition = stickyParent.top
            }
            if (stickyPosition < 0) return@post

            if (stickyView.parent == stickyHolder && scrollY < stickyPosition - offsetY) {
                stickyHolder.removeView(stickyView)
                stickyParent.addView(stickyView)
            } else if (stickyView.parent == stickyParent && scrollY >= stickyPosition - offsetY) {
                stickyParent.removeView(stickyView)
                stickyHolder.addView(stickyView)
            }
        }
    }
}