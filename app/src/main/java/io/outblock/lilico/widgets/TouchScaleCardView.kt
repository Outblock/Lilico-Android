package io.outblock.lilico.widgets

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView
import com.sackcentury.shinebuttonlib.listener.SimpleAnimatorListener
import io.outblock.lilico.utils.createScaleObjectAnimation

open class TouchScaleCardView : MaterialCardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    private var touchDownAnim: ObjectAnimator? = null
    private var touchUpAnim: ObjectAnimator? = null

    private var touchHasUp = false

    private var longPressListener: OnLongClickListener? = null

    private var isScaleEnable = true

    init {
        isClickable = true
        isFocusable = true
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        this.longPressListener = l
        super.setOnLongClickListener {
            val longClick = longPressListener?.onLongClick(it) ?: false
            if (longClick) {
                touchUp()
            }
            longClick
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (!isScaleEnable) {
            return super.dispatchTouchEvent(event)
        }
        if (touchHasUp && event.action != MotionEvent.ACTION_DOWN) {
            return super.dispatchTouchEvent(event)
        }
        fun inView(): Boolean {
            with(event) {
                return x > 0 && x <= width && y > 0 && y <= height
            }
        }
        when {
            event.action == MotionEvent.ACTION_DOWN -> {
                touchHasUp = false
                touchDown()
            }
            event.action == MotionEvent.ACTION_UP || !inView() -> {
                touchHasUp = true
                touchUp()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun setScaleEnable(isEnable: Boolean) {
        this.isScaleEnable = isEnable
    }

    private fun touchDown() {
        touchUpAnim?.cancel()
        touchDownAnim?.cancel()
        touchDownAnim = createScaleObjectAnimation(this, scaleX, 0.95f, 70)
        touchDownAnim?.start()
    }

    private fun touchUp() {
        fun start() {
            touchUpAnim = createScaleObjectAnimation(this, scaleX, 1f, 70)
            touchDownAnim?.cancel()
            touchUpAnim?.start()
        }

        touchUpAnim?.cancel()
        if (touchDownAnim?.isRunning == true) {
            touchDownAnim?.addListener(object : SimpleAnimatorListener() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    start()
                }
            })
        } else {
            start()
        }
    }

    companion object {
        private const val TAG = "VpnSwitchButton"
    }
}