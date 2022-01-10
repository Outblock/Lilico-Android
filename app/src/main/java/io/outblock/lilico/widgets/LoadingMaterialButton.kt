package io.outblock.lilico.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.graphics.withTranslation
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color

class LoadingMaterialButton : MaterialButton {

    private var xShift = 0f

    private val progressDrawable by lazy { createProgressDrawable() }

    private var progressVisible = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr)

    private fun createProgressDrawable(): IndeterminateDrawable<CircularProgressIndicatorSpec> {
        val indicator = CircularProgressIndicatorSpec(context, null).apply {
            indicatorColors = intArrayOf(R.color.white.res2color())
            trackThickness = 2.dp2px().toInt()
            indicatorSize = 20.dp2px().toInt()
        }
        return IndeterminateDrawable.createCircularDrawable(context, indicator).apply { setVisible(true, true) }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (progressVisible) {
            xShift = (width
              - icon.intrinsicWidth
              - paint.measureText(text.toString())
              - iconPadding
              - ViewCompat.getPaddingStart(this)
              - ViewCompat.getPaddingEnd(this)) / 2f
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.withTranslation(xShift) {
            super.onDraw(canvas)
        }
    }

    fun setProgressVisible(isVisible: Boolean) {
        progressVisible = isVisible
        if (progressVisible) {
            icon = progressDrawable
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        } else {
            icon = null
            iconGravity = ICON_GRAVITY_TEXT_START
        }
        requestLayout()
    }

    fun isProgressVisible() = progressVisible
}