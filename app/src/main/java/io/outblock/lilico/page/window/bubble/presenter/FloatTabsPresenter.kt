package io.outblock.lilico.page.window.bubble.presenter

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.WindowBubbleBinding
import io.outblock.lilico.page.window.bubble.BubbleViewModel
import io.outblock.lilico.page.window.bubble.adapter.FloatTabsAdapter
import io.outblock.lilico.page.window.bubble.model.FloatTabsModel
import io.outblock.lilico.page.window.bubble.tools.bubbleTabs
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class FloatTabsPresenter(
    private val binding: WindowBubbleBinding,
    private val viewModel: BubbleViewModel,
) : BasePresenter<FloatTabsModel> {

    private val adapter by lazy { FloatTabsAdapter() }

    override fun bind(model: FloatTabsModel) {
        model.showTabs?.let { showTabs() }
        model.closeTabs?.let { closeTabs() }
        model.onTabChange?.let {
            adapter.setNewDiffData(bubbleTabs())
            if (bubbleTabs().isEmpty()) {
                closeTabs()
                viewModel.onHideFloatTabs()
            }
        }
    }

    private fun showTabs() {
        binding.bubbleStackWrapper.setVisible(true)
        binding.bubbleStackWrapper.setOnClickListener {
            closeTabs()
            viewModel.onHideFloatTabs()
        }

        initRecyclerView()

        adapter.setNewDiffData(bubbleTabs())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun closeTabs() {
        adapter.setNewDiffData(emptyList())
        binding.recyclerView.adapter = adapter
        binding.bubbleStackWrapper.setVisible(false)
    }

    private fun initRecyclerView() {
        with(binding.recyclerView) {
            if (adapter == this@FloatTabsPresenter.adapter && layoutManager != null) {
                return
            }
            adapter = this@FloatTabsPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 10.dp2px().toInt()))
        }
    }
}