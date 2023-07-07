package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isProfileSwitchTipsShown
import io.outblock.lilico.utils.setProfileSwitchTipsShown

class ProfileSwitchTipsBanner : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        setVisible(false)
        ioScope {
            if (!isProfileSwitchTipsShown()) {
                setup()
            }
        }
    }

    private fun setup() {
        setVisible(true)
        LayoutInflater.from(context).inflate(R.layout.view_profile_tips_banner, this)

        findViewById<View>(R.id.close_button).setOnClickListener {
            ioScope { setProfileSwitchTipsShown() }
            setVisible(false)
        }
    }
}