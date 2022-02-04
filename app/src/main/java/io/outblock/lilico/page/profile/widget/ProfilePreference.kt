package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.setVisible

open class ProfilePreference : FrameLayout {

    @DrawableRes
    internal val icon: Int
    internal val iconEnable: Boolean
    internal val title: String
    internal val desc: String
    internal val isArrowVisible: Boolean

    private val rootView by lazy { findViewById<ViewGroup>(R.id.rootView) }
    private val iconView by lazy { findViewById<ImageView>(R.id.icon_view) }
    private val titleView by lazy { findViewById<TextView>(R.id.title_view) }
    private val descView by lazy { findViewById<TextView>(R.id.desc_view) }
    private val arrowView by lazy { findViewById<View>(R.id.arrow_view) }
    private val extendContainer by lazy { findViewById<ViewGroup>(R.id.extend_container) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.ProfilePreference, defStyleAttr, 0)
        iconEnable = styleAttrs.getBoolean(R.styleable.ProfilePreference_iconEnable, false)
        icon = styleAttrs.getResourceId(R.styleable.ProfilePreference_icon, 0)
        title = styleAttrs.getString(R.styleable.ProfilePreference_title).orEmpty()
        desc = styleAttrs.getString(R.styleable.ProfilePreference_desc).orEmpty()
        isArrowVisible = styleAttrs.getBoolean(R.styleable.ProfilePreference_isArrowVisible, false)
        styleAttrs.recycle()

        LayoutInflater.from(context).inflate(R.layout.widget_profile_item, this)

        setup()
    }

    fun setExtendView(view: View) {
        extendContainer.addView(view, ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    override fun setOnClickListener(l: OnClickListener?) {
        rootView.setOnClickListener(l)
    }

    private fun setup() {
        iconView.setVisible(iconEnable)
        if (icon != 0) {
            iconView.setImageResource(icon)
        }
        arrowView.setVisible(isArrowVisible)

        descView.text = desc
        titleView.text = title
    }
}