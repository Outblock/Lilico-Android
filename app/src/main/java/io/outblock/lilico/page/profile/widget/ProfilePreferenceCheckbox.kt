package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.uiScope

class ProfilePreferenceCheckbox : ProfilePreference {

    private val checkbox by lazy { LayoutInflater.from(context).inflate(R.layout.view_settings_checkbox, this, false) as CheckBox }

    private var onCheckedChangeListener: ((isChecked: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        setBeginningView(checkbox, ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        descView.setVisible(false)
        setOnClickListener { toggleSwitch() }
    }

    fun setChecked(isChecked: Boolean) {
        uiScope {
            if (isChecked == checkbox.isChecked) {
                return@uiScope
            }
            checkbox.isChecked = isChecked
            descView.setVisible(isChecked)
        }
    }

    fun isChecked() = checkbox.isChecked

    fun setOnCheckedChangeListener(listener: (isChecked: Boolean) -> Unit) {
        this.onCheckedChangeListener = listener
    }

    private fun toggleSwitch() {
        if (checkbox.isChecked) {
            return
        }
        checkbox.isChecked = !checkbox.isChecked
        descView.setVisible(checkbox.isChecked)
        onCheckedChangeListener?.invoke(checkbox.isChecked)
    }
}