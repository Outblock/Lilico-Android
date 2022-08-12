package io.outblock.lilico.page.nft.nftlist.widget

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import io.outblock.lilico.page.nft.nftlist.findSwipeRefreshLayout
import io.outblock.lilico.utils.logd
import kotlin.math.absoluteValue

class NftCoordinatorLayout : CoordinatorLayout, GestureDetector.OnGestureListener {

    private val gestureDetector by lazy { GestureDetector(context, this) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        gestureDetector.onTouchEvent(ev)
//        if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
//            findSwipeRefreshLayout(this)?.isEnabled = true
//        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDown(e: MotionEvent?): Boolean = false

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean = false

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        logd("xxx", "NftCoordinatorLayout onScroll distanceX:$distanceX-distanceY: $distanceY")
        if (distanceX.absoluteValue > distanceY.absoluteValue) {
            logd("xxx", "NftCoordinatorLayout requestDisallowInterceptTouchEvent")
            requestDisallowInterceptTouchEvent(true)
            findSwipeRefreshLayout(this)?.isEnabled = false
            return true
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {}

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false
}