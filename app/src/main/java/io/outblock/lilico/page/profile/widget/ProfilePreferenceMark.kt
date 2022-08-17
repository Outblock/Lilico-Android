package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.setVisible

class ProfilePreferenceMark : ProfilePreference {

    private val markView by lazy { LayoutInflater.from(context).inflate(R.layout.view_profile_preference_mark, this, false) as TextView }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        setExtendView(markView)
        markView.setVisible(false)
        descView.setVisible(false)
    }

    fun setMarkText(markText: String) {
        markView.setVisible(markText.isNotBlank())
        markView.text = markText
    }
}