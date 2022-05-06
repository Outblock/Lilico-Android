package io.outblock.lilico.page.browser.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemBrowserFloatTabsBinding
import io.outblock.lilico.page.browser.browserViewModel
import io.outblock.lilico.page.browser.expandBrowser
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.browser.tools.changeBrowserTab

class BrowserFloatTabsItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<BrowserTab> {

    private val binding by lazy { ItemBrowserFloatTabsBinding.bind(view) }

    override fun bind(model: BrowserTab) {
        with(binding) {
            Glide.with(iconView).load(model.webView.favicon).into(iconView)
            titleView.text = model.webView.title
            closeButton.setOnClickListener { browserViewModel()?.popTab(model) }
            contentView.setOnClickListener {
                changeBrowserTab(model.id)
                browserViewModel()?.onHideFloatTabs()
                browserViewModel()?.onTabChange()
                expandBrowser()
            }
        }
    }
}