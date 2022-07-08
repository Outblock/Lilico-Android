package io.outblock.lilico.page.transaction.record.presenter

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityTransactionRecordBinding
import io.outblock.lilico.page.transaction.record.TransactionRecordActivity
import io.outblock.lilico.page.transaction.record.adapter.TransactionRecordListAdapter
import io.outblock.lilico.page.transaction.record.model.TransactionRecordPageModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class TransactionRecordPresenter(
    private val binding: ActivityTransactionRecordBinding,
    private val activity: TransactionRecordActivity,
) : BasePresenter<TransactionRecordPageModel> {

    private val adapter by lazy { TransactionRecordListAdapter() }

    init {
        with(binding.recyclerView) {
            adapter = this@TransactionRecordPresenter.adapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 4.dp2px().toInt()))
        }
        binding.refreshLayout.setColorSchemeColors(R.color.salmon_primary.res2color())
        binding.refreshLayout.post { binding.refreshLayout.isRefreshing = true }
    }

    override fun bind(model: TransactionRecordPageModel) {
        model.data?.let {
            binding.refreshLayout.isRefreshing = false
            adapter.setNewDiffData(it)
        }
    }
}