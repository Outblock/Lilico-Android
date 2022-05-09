package io.outblock.lilico.page.window.bubble.presenter

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemBrowserFloatTabsBinding
import io.outblock.lilico.page.window.bubble.model.BubbleItem
import io.outblock.lilico.page.window.bubble.model.icon
import io.outblock.lilico.page.window.bubble.model.title

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
//                changeBrowserTab(model.id)
//                browserViewModel()?.onHideFloatTabs()
//                browserViewModel()?.onTabChange()
//                expandBrowser()
            }
        }
    }
}