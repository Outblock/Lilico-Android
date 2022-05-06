package io.outblock.lilico.page.browser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.page.browser.presenter.BrowserFloatTabsItemPresenter
import io.outblock.lilico.page.browser.tools.BrowserTab

class BrowserFloatTabsAdapter : BaseAdapter<BrowserTab>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BrowserFloatTabsItemPresenter(parent.inflate(R.layout.item_browser_float_tabs))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BrowserFloatTabsItemPresenter).bind(getItem(position))
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<BrowserTab>() {
    override fun areItemsTheSame(oldItem: BrowserTab, newItem: BrowserTab): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BrowserTab, newItem: BrowserTab): Boolean {
        return false
    }
}