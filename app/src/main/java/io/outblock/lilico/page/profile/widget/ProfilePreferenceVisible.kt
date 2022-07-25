package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.setVisible

class ProfilePreferenceVisible : ProfilePreference {

    private val extendView by lazy { LayoutInflater.from(context).inflate(R.layout.view_profile_preference_visible, this, false) as ViewGroup }

    private val visibleWrapper by lazy { findViewById<ViewGroup>(R.id.visible_wrapper) }
    private val invisibleWrapper by lazy { findViewById<ViewGroup>(R.id.invisible_wrapper) }

    private val visibleStateView by lazy { findViewById<View>(R.id.visible_check_state_view) }
    private val invisibleStateView by lazy { findViewById<View>(R.id.invisible_check_state_view) }

    private var onStateChangeListener: ((isVisible: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        setExtendView(extendView)

        visibleWrapper.setOnClickListener {
            onStateChangeListener?.invoke(true)
        }
        invisibleWrapper.setOnClickListener {
            onStateChangeListener?.invoke(false)
        }
    }

    fun updateState(isVisible: Boolean) {
        visibleStateView.setVisible(isVisible)
        invisibleStateView.setVisible(!isVisible)
    }

    fun setOnStateChangeListener(listener: (isVisible: Boolean) -> Unit) {
        onStateChangeListener = listener
    }
}