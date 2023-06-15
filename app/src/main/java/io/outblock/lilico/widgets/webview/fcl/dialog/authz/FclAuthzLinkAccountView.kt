package io.outblock.lilico.widgets.webview.fcl.dialog.authz

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.outblock.lilico.R
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.databinding.DialogLinkAccountBinding
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.browser.loadFavicon
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel

class FclAuthzLinkAccountView : FrameLayout, OnTransactionStateChange {
    private val binding: DialogLinkAccountBinding

    private lateinit var data: FclDialogModel

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_link_account, this, false)
        addView(view)
        binding = DialogLinkAccountBinding.bind(view)
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    fun setup(data: FclDialogModel, approveCallback: ((isApprove: Boolean) -> Unit)) {
        this.data = data
        binding.setup(data)

        binding.root.requestFocus()
        binding.startButton.setOnProcessing {
            binding.titleView.setText(R.string.account_linking)
            approveCallback.invoke(true)
        }
        showDefaultUI()
    }

    override fun onTransactionStateChange() {
        val transactionState = TransactionStateManager.getTransactionStateList()
            .firstOrNull { it.type == TransactionState.TYPE_FCL_TRANSACTION } ?: return
        if (transactionState.isProcessing()) {
            // ignore
        } else if (transactionState.isFailed()) {
            showFailedUI()
        } else if (transactionState.isSuccess()) {
            showSuccessUI()
        }
    }

    private fun showFailedUI() {
        with(binding) {
            descView.text = ""
            titleView.setText(R.string.link_fail)
            line.setBackgroundResource(R.drawable.bg_link_account_error_line)
            startButton.setVisible(false)
            tryAgainButton.setVisible(true)
            errorXIcon.setVisible(true)
            point1.setVisible(false)
            point2.setVisible(false)
            tryAgainButton.setOnClickListener { }
        }
    }

    private fun showSuccessUI() {
        with(binding) {
            descView.setText(R.string.link_account_success_desc)
            titleView.setText(R.string.successful)
            defaultLayout.setVisible(false)
            successLayout.setVisible(true)
            successStartButton.setOnClickListener { }
        }
    }

    private fun showDefaultUI() {
        with(binding) {
            descView.text = context.getString(R.string.link_account_desc, data.title.orEmpty())
            titleView.setText(R.string.account_linking)
            line.setBackgroundResource(R.drawable.bg_link_account_line)
            defaultLayout.setVisible()
            startButton.setVisible()
            point1.setVisible()
            point2.setVisible()
            successLayout.setVisible(false)
            tryAgainButton.setVisible(false)
            errorXIcon.setVisible(false)
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