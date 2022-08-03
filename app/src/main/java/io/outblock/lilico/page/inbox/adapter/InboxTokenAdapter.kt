package io.outblock.lilico.page.inbox.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.network.model.InboxToken
import io.outblock.lilico.page.inbox.presenter.InboxTokenItemPresenter

class InboxTokenAdapter : BaseAdapter<InboxToken>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InboxTokenItemPresenter(parent.inflate(R.layout.item_inbox_token))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InboxTokenItemPresenter).bind(getItem(position))
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<InboxToken>() {
    override fun areItemsTheSame(oldItem: InboxToken, newItem: InboxToken): Boolean {
        return oldItem.coinAddress == newItem.coinAddress
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: InboxToken, newItem: InboxToken): Boolean {
        return oldItem == newItem
    }
}