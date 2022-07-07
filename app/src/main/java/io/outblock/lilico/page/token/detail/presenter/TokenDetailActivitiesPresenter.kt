package io.outblock.lilico.page.token.detail.presenter

import android.transition.TransitionManager
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutTokenDetailActivitiesBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.network.flowscan.contractId
import io.outblock.lilico.page.token.detail.model.TokenDetailActivitiesModel
import io.outblock.lilico.page.transaction.record.TransactionRecordActivity
import io.outblock.lilico.page.transaction.record.adapter.TransactionRecordListAdapter
import io.outblock.lilico.utils.extensions.setVisible


class TokenDetailActivitiesPresenter(
    private val activity: AppCompatActivity,
    private val binding: LayoutTokenDetailActivitiesBinding,
    private val coin: FlowCoin,
) : BasePresenter<TokenDetailActivitiesModel> {

    private val adapter by lazy { TransactionRecordListAdapter() }

    init {
        with(binding.recyclerView) {
            adapter = this@TokenDetailActivitiesPresenter.adapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.activitiesMoreButton.setOnClickListener { TransactionRecordActivity.launch(activity, coin.contractId()) }
    }

    override fun bind(model: TokenDetailActivitiesModel) {
        model.recordList?.let {
            TransitionManager.beginDelayedTransition(binding.root.parent as ViewGroup)
            binding.root.setVisible(it.isNotEmpty())
            adapter.setNewDiffData(it)
        }
    }

}