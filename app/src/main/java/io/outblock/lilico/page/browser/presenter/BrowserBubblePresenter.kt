package io.outblock.lilico.page.browser.presenter

import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutBrowserBinding
import io.outblock.lilico.page.browser.BrowserViewModel
import io.outblock.lilico.page.browser.model.BrowserBubbleModel
import io.outblock.lilico.page.browser.onBrowserBubbleClick
import io.outblock.lilico.page.browser.releaseBrowser
import io.outblock.lilico.widgets.floatwindow.widgets.WindowRemoveLayout

class BrowserBubblePresenter(
    private val binding: LayoutBrowserBinding,
    private val viewModel: BrowserViewModel,
) : BasePresenter<BrowserBubbleModel> {

    private val removeLayout by lazy { WindowRemoveLayout(binding.root, binding.floatBubble) { releaseBrowser() } }

    init {
        with(binding) {
            floatBubble.setOnClickListener { onBrowserBubbleClick() }
            floatBubble.setOnDragListener(removeLayout)
        }
    }

    override fun bind(model: BrowserBubbleModel) {

    }
}