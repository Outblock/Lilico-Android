package io.outblock.lilico.page.transaction.record.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.page.transaction.record.presenter.TransactionRecordItemPresenter

class TransactionRecordListAdapter : BaseAdapter<TransactionRecord>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TransactionRecordItemPresenter(parent.inflate(R.layout.item_transaction_record))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TransactionRecordItemPresenter).bind(getItem(position))
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<TransactionRecord>() {
    override fun areItemsTheSame(oldItem: TransactionRecord, newItem: TransactionRecord): Boolean {
        return oldItem.transaction.hash == newItem.transaction.hash
    }

    override fun areContentsTheSame(oldItem: TransactionRecord, newItem: TransactionRecord): Boolean {
        return oldItem == newItem
    }
}