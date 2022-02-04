package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.SwitchCompat
import io.outblock.lilico.R

class ProfilePreferenceSwitch : ProfilePreference {

    private val switchView by lazy { LayoutInflater.from(context).inflate(R.layout.view_switch, this, false) as SwitchCompat }

    private var onCheckedChangeListener: ((isChecked: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        setExtendView(switchView)
        setOnClickListener { toggleSwitch() }
    }

    fun setChecked(isChecked: Boolean) {
        switchView.isChecked = isChecked
    }

    fun setOnCheckedChangeListener(listener: (isChecked: Boolean) -> Unit) {
        this.onCheckedChangeListener = listener
    }

    private fun toggleSwitch() {
        switchView.isChecked = !switchView.isChecked
        onCheckedChangeListener?.invoke(switchView.isChecked)
    }
}