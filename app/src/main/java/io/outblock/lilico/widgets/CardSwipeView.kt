package io.outblock.lilico.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.children
import io.outblock.lilico.utils.logd
import kotlin.math.pow

class CardSwipeView : MotionLayout {

    private var downX = 0.0f
    private var downY = 0.0f

    private var onClickListener: OnClickListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr)


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        requestDisallowInterceptTouchEvent(true)
        if (ev.action == MotionEvent.ACTION_DOWN) {
            downX = ev.x
            downY = ev.y
        }

        if (ev.action == MotionEvent.ACTION_UP) {
            val distance = (ev.x - downX).pow(2) + (ev.y - downY).pow(2)
            logd("CardSwipeView", "distance:$distance")
            if (distance < 36) {
                onClickListener?.onClick(this)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.onClickListener = l
    }

    fun refresh() {
        children.forEach { it.visibility = VISIBLE }
    }
}