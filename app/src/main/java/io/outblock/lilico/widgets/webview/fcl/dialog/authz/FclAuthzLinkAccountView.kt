package io.outblock.lilico.widgets.webview.fcl.dialog.authz

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.outblock.lilico.R
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.databinding.DialogLinkAccountBinding
import io.outblock.lilico.page.browser.loadFavicon
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel

class FclAuthzLinkAccountView : FrameLayout {
    private val binding: DialogLinkAccountBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_link_account, this, false)
        addView(view)
        binding = DialogLinkAccountBinding.bind(view)
    }

    fun setup(data: FclDialogModel, approveCallback: ((isApprove: Boolean) -> Unit)) {
        binding.setup(data)

        binding.root.requestFocus()
        binding.startButton.setOnProcessing {
            approveCallback.invoke(true)
        }
    }
}

private fun DialogLinkAccountBinding.setup(fcl: FclDialogModel) {
    dappIcon.loadFavicon(fcl.logo ?: fcl.url?.toFavIcon())
    dappName.text = fcl.title
    ioScope {
        val userinfo = userInfoCache().read()
        uiScope {
            walletIcon.loadAvatar(userinfo?.avatar.orEmpty())
            walletName.text = userinfo?.nickname
        }
    }
}