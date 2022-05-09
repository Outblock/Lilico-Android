package io.outblock.lilico.page.window.bubble.presenter

import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.WindowBubbleBinding
import io.outblock.lilico.page.browser.releaseBrowser
import io.outblock.lilico.page.window.bubble.BubbleViewModel
import io.outblock.lilico.page.window.bubble.model.BubbleModel
import io.outblock.lilico.page.window.bubble.tools.bubbleTabs
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.floatwindow.widgets.WindowRemoveLayout

class BubblePresenter(
    private val binding: WindowBubbleBinding,
    private val viewModel: BubbleViewModel,
) : BasePresenter<BubbleModel> {

    private val removeLayout by lazy { WindowRemoveLayout(binding.root, binding.floatBubble) { releaseBrowser() } }

    init {
        with(binding) {
            floatBubble.setOnClickListener {
//                onBrowserBubbleClick()
            }
            floatBubble.setOnDragListener(removeLayout)
            floatBubble.setVisible(bubbleTabs().isNotEmpty(), invisible = true)
        }
    }

    override fun bind(model: BubbleModel) {
        model.onTabChange?.let { onTabChange() }
    }

    private fun onTabChange() {
        with(binding) {
            floatBubble.setVisible(bubbleTabs().isNotEmpty() && !bubbleStackWrapper.isVisible(), invisible = true)
        }
    }
}