package io.outblock.lilico.page.window.bubble.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.window.bubble.model.BubbleItem
import io.outblock.lilico.page.window.bubble.presenter.FloatTabsItemPresenter

class FloatTabsAdapter : BaseAdapter<BubbleItem>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FloatTabsItemPresenter(parent.inflate(R.layout.item_browser_float_tabs))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FloatTabsItemPresenter).bind(getItem(position))
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<BubbleItem>() {
    override fun areItemsTheSame(oldItem: BubbleItem, newItem: BubbleItem): Boolean {
        if (oldItem.data is BrowserTab && newItem.data is BrowserTab) {
            return oldItem.data.id == newItem.data.id
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BubbleItem, newItem: BubbleItem): Boolean {
        return false
    }
}