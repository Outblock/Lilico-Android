package io.outblock.lilico.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.RadioButton
import android.widget.RadioGroup
import io.outblock.lilico.R
import io.outblock.lilico.utils.logd

class SegmentControl : RadioGroup {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    private var tabs = listOf<String>()

    init {
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL

        setBackgroundResource(R.drawable.bg_segment_control)
        background.alpha = (255 * 0.24).toInt()
        setOnCheckedChangeListener { group, checkedId ->
            logd(TAG,"setOnCheckedChangeListener group=$group, checkedId=$checkedId")
        }
    }

    fun setTabs(tabs: List<String>) {
        this.tabs = tabs
        removeAllViews()
        setupTabs()
    }

    private fun setupTabs() {
        for (tab in tabs) {
            with(RadioButton(context)) {
                setButtonDrawable(R.color.transparent)
                minWidth = 0
                text = tab
                addView(this)
            }
        }
    }

    companion object{
        private val TAG = SegmentControl::class.java.simpleName
    }
}