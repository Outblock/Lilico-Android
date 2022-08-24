package io.outblock.lilico.page.transaction.record.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.network.model.TransferRecord
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.page.transaction.record.presenter.TransactionRecordItemPresenter
import io.outblock.lilico.page.transaction.record.presenter.TransferRecordItemPresenter

private const val TYPE_TRANSACTION = 0
private const val TYPE_TRANSFER = 1
private const val TYPE_NONE = -1

class TransactionRecordListAdapter : BaseAdapter<Any>(diffCallback) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionRecord -> TYPE_TRANSACTION
            is TransferRecord -> TYPE_TRANSFER
            else -> TYPE_NONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TRANSACTION -> TransactionRecordItemPresenter(parent.inflate(R.layout.item_transaction_record))
            TYPE_TRANSFER -> TransferRecordItemPresenter(parent.inflate(R.layout.item_transfer_record))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionRecordItemPresenter -> holder.bind(getItem(position) as TransactionRecord)
            is TransferRecordItemPresenter -> holder.bind(getItem(position) as TransferRecord)
        }
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is TransactionRecord && newItem is TransactionRecord) {
            return oldItem.transaction.hash == newItem.transaction.hash
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is TransactionRecord && newItem is TransactionRecord) {
            return oldItem == newItem
        }
        return false
    }
}