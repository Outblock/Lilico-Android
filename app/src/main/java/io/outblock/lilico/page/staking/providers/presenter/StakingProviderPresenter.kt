package io.outblock.lilico.page.staking.providers.presenter

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.databinding.ActivityStakeProviderBinding
import io.outblock.lilico.page.staking.providers.adapter.StakeProviderAdapter
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class StakingProviderPresenter(
    private val binding: ActivityStakeProviderBinding,
) {

    private val adapter = StakeProviderAdapter()

    init {
        with(binding.recyclerView) {
            adapter = this@StakingProviderPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                ColorDividerItemDecoration(Color.TRANSPARENT, 8.dp2px().toInt())
            )
        }
    }

    fun bind(data: List<Any>) {
        adapter.setNewDiffData(data)
    }
}