package io.outblock.lilico.page.window.bubble.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemBrowserFloatTabsBinding
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_ADD_TOKEN
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_FCL_TRANSACTION
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_TRANSFER_COIN
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_TRANSFER_NFT
import io.outblock.lilico.page.browser.browserViewModel
import io.outblock.lilico.page.browser.expandBrowser
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.browser.tools.changeBrowserTab
import io.outblock.lilico.page.dialog.processing.coinenable.CoinEnableProcessingDialog
import io.outblock.lilico.page.dialog.processing.fcl.FclTransactionProcessingDialog
import io.outblock.lilico.page.dialog.processing.send.SendProcessingDialog
import io.outblock.lilico.page.window.bubble.bubbleViewModel
import io.outblock.lilico.page.window.bubble.model.BubbleItem
import io.outblock.lilico.page.window.bubble.model.icon
import io.outblock.lilico.page.window.bubble.model.title
import io.outblock.lilico.page.window.bubble.tools.popBubbleStack
import io.outblock.lilico.utils.extensions.setVisible

class FloatTabsItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<BubbleItem> {

    private val binding by lazy { ItemBrowserFloatTabsBinding.bind(view) }

    override fun bind(model: BubbleItem) {
        with(binding) {
            BaseActivity.getCurrentActivity()?.let { Glide.with(it).load(model.icon()).into(iconView) }
            titleView.text = model.title()
            progressBar.setVisible(model.data is TransactionState)
            (model.data as? TransactionState)?.let { progressBar.setProgressWithAnimation(it.progress(), duration = 200) }
            closeButton.setOnClickListener { popBubbleStack(model.data) }
            contentView.setOnClickListener {
                bubbleViewModel()?.onHideFloatTabs()
                showTabContent(model.data)
            }
        }
    }

    private fun showTabContent(data: Any) {
        when (data) {
            is BrowserTab -> showBrowser(data)
            is TransactionState -> showTransactionStateDialog(data)
        }
    }

    private fun showTransactionStateDialog(data: TransactionState) {
        when (data.type) {
            TYPE_TRANSFER_COIN, TYPE_TRANSFER_NFT -> SendProcessingDialog.show(data)
            TYPE_ADD_TOKEN -> CoinEnableProcessingDialog.show(data)
            TYPE_FCL_TRANSACTION -> FclTransactionProcessingDialog.show(data.transactionId)
        }
    }

    private fun showBrowser(tab: BrowserTab) {
        changeBrowserTab(tab.id)
        browserViewModel()?.onTabChange()
        expandBrowser()
    }
}