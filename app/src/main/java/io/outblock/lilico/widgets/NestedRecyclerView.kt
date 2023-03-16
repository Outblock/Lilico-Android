package io.outblock.lilico.widgets

import android.R.attr.action
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.ViewCompat.TYPE_TOUCH
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.utils.ScreenUtils


/**
 * use for contained in NestedScrollView
 */
class NestedRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
//        isNestedScrollingEnabled = false
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        with(layoutParams) {
//            height = ScreenUtils.getScreenHeight()
//            layoutParams = this
//        }
//    }

}