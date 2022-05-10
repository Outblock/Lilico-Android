package io.outblock.lilico.page.window.bubble.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemBrowserFloatTabsBinding
import io.outblock.lilico.page.browser.browserViewModel
import io.outblock.lilico.page.browser.expandBrowser
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.browser.tools.changeBrowserTab
import io.outblock.lilico.page.window.bubble.bubbleViewModel
import io.outblock.lilico.page.window.bubble.model.BubbleItem
import io.outblock.lilico.page.window.bubble.model.icon
import io.outblock.lilico.page.window.bubble.model.title
import io.outblock.lilico.page.window.bubble.tools.popBubbleStack

class FloatTabsItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<BubbleItem> {

    private val binding by lazy { ItemBrowserFloatTabsBinding.bind(view) }

    override fun bind(model: BubbleItem) {
        with(binding) {
            BaseActivity.getCurrentActivity()?.let { Glide.with(it).load(model.icon()).into(iconView) }
            titleView.text = model.title()
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
        }
    }

    private fun showBrowser(tab: BrowserTab) {
        changeBrowserTab(tab.id)
        browserViewModel()?.onTabChange()
        expandBrowser()
    }
}