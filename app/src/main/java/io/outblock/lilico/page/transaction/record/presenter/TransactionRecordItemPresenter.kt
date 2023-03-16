package io.outblock.lilico.page.transaction.record.presenter

import android.annotation.SuppressLint
import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemTransactionRecordBinding
import io.outblock.lilico.network.flowscan.model.FlowScanTransaction
import io.outblock.lilico.page.browser.openInFlowScan
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.findActivity
import org.joda.time.format.ISODateTimeFormat

class TransactionRecordItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<TransactionRecord> {

    private val binding by lazy { ItemTransactionRecordBinding.bind(view) }

    override fun bind(model: TransactionRecord) {
        with(binding) {
            titleView.setText(R.string.transaction_common_title)
            iconView.setImageResource(R.drawable.ic_transaction_default)
            bindStatus(model.transaction)
            bindDesc(model.transaction)
        }
        binding.root.setOnClickListener { openInFlowScan(findActivity(view)!!, model.transaction.hash!!) }
    }

    @SuppressLint("SetTextI18n")
    private fun ItemTransactionRecordBinding.bindDesc(transaction: FlowScanTransaction) {
        val time = ISODateTimeFormat.dateTimeParser().parseDateTime(transaction.time!!).toString("MMM dd")
        val contractInteractions = transaction.contractInteractions?.mapNotNull { it?.identifier }?.joinToString(" ") { it } ?: ""

        descView.text = "$time${if (contractInteractions.isBlank()) "" else " · $contractInteractions"}"
    }

    private fun ItemTransactionRecordBinding.bindStatus(transaction: FlowScanTransaction) {
        val color = when (transaction.status.orEmpty()) {
            "Sealed" -> if (transaction.error.isNullOrBlank()) R.color.success3.res2color() else R.color.warning2.res2color()
            else -> R.color.neutrals6.res2color()
        }

        val text = when (transaction.status.orEmpty()) {
            "Sealed" -> if (transaction.error.isNullOrBlank()) transaction.status else "Error"
            else -> "Pending"
        }

        statusView.setTextColor(color)
        statusView.text = text
    }
}