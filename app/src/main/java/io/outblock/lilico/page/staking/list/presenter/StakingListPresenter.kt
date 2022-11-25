package io.outblock.lilico.page.staking.list.presenter

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.databinding.ActivityStakeListBinding
import io.outblock.lilico.page.staking.list.adapter.StakeListAdapter
import io.outblock.lilico.page.staking.providers.StakingProviderActivity
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class StakingListPresenter(
    private val binding: ActivityStakeListBinding,
) {

    private val adapter = StakeListAdapter()

    init {
        with(binding.recyclerView) {
            adapter = this@StakingListPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                ColorDividerItemDecoration(Color.TRANSPARENT, 12.dp2px().toInt())
            )
        }
        binding.button.setOnClickListener { StakingProviderActivity.launch(binding.root.context) }
    }

    fun bind(data: List<Any>) {
        adapter.setNewDiffData(data)
    }
}