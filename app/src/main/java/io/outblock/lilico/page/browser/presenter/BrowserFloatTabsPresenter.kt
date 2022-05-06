package io.outblock.lilico.page.browser.presenter

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.databinding.LayoutBrowserFloatTabsBinding
import io.outblock.lilico.page.browser.BrowserViewModel
import io.outblock.lilico.page.browser.adapter.BrowserFloatTabsAdapter
import io.outblock.lilico.page.browser.model.BrowserFloatTabsModel
import io.outblock.lilico.page.browser.releaseBrowser
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.browser.tools.browserTabs
import io.outblock.lilico.page.browser.tools.browserTabsCount
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.fadeTransition
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class BrowserFloatTabsPresenter(
    private val binding: LayoutBrowserBinding,
    private val viewModel: BrowserViewModel,
) : BasePresenter<BrowserFloatTabsModel> {

    private val floatBinding by lazy { LayoutBrowserFloatTabsBinding.bind(binding.floatTabsStub.inflate()) }

    private val adapter by lazy { BrowserFloatTabsAdapter() }

    override fun bind(model: BrowserFloatTabsModel) {
        model.showTabs?.let { showTabs() }
        model.closeTabs?.let { closeTabs() }
        model.removeTab?.let { removeTab(it) }
    }

    private fun removeTab(tab: BrowserTab) {
        val tabs = browserTabs()
        if (tabs.isEmpty()) {
            closeTabs()
            viewModel.onHideFloatTabs()
        } else {
            adapter.setNewDiffData(tabs)
        }
        if (browserTabsCount() == 0) {
            releaseBrowser()
        }
    }

    private fun showTabs() {
        binding.root.fadeTransition(duration = 200)
        floatBinding.root.setVisible(true)
        floatBinding.root.setOnClickListener {
            closeTabs()
            viewModel.onHideFloatTabs()
        }

        initRecyclerView()

        adapter.setNewDiffData(browserTabs())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun closeTabs() {
        adapter.setNewDiffData(emptyList())
        floatBinding.recyclerView.adapter = adapter
        binding.root.fadeTransition(duration = 200)
        floatBinding.root.setVisible(false)
    }

    private fun initRecyclerView() {
        with(floatBinding.recyclerView) {
            if (adapter == this@BrowserFloatTabsPresenter.adapter) {
                return
            }
            adapter = this@BrowserFloatTabsPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 10.dp2px().toInt()))
        }
    }
}