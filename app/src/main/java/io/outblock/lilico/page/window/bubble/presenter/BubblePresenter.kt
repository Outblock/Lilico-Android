package io.outblock.lilico.page.window.bubble.presenter

import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.WindowBubbleBinding
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.page.window.bubble.BubbleViewModel
import io.outblock.lilico.page.window.bubble.model.BubbleModel
import io.outblock.lilico.page.window.bubble.model.icon
import io.outblock.lilico.page.window.bubble.onBubbleClick
import io.outblock.lilico.page.window.bubble.tools.bubbleTabs
import io.outblock.lilico.page.window.bubble.tools.clearBubbleTabs
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.DraggableLayout
import io.outblock.lilico.widgets.floatwindow.widgets.WindowRemoveLayout

class BubblePresenter(
    private val binding: WindowBubbleBinding,
    private val viewModel: BubbleViewModel,
) : BasePresenter<BubbleModel> {

    private val removeLayout by lazy { WindowRemoveLayout(binding.root, binding.floatBubble) { clearBubbleTabs() } }

    init {
        with(binding) {
            floatBubble.setOnClickListener { onBubbleClick() }
            floatBubble.addOnDragListener(removeLayout)
            floatBubble.addOnDragListener(BubbleDragListener())
            floatBubble.setVisible(bubbleTabs().isNotEmpty(), invisible = true)
        }
    }

    override fun bind(model: BubbleModel) {
        model.onTabChange?.let { onTabChange() }
        model.onVisibleChange?.let { onVisibleChange(it) }
    }

    private fun onVisibleChange(isVisible: Boolean) {
        binding.floatBubble.setVisible(isVisible && bubbleTabs().isNotEmpty(), invisible = true)
    }

    private fun onTabChange() {
        with(binding) {
            floatBubble.setVisible(bubbleTabs().isNotEmpty() && !bubbleStackWrapper.isVisible(), invisible = true)
            val tab = bubbleTabs().lastOrNull() ?: return
            Glide.with(Env.getApp()).load(tab.icon()).into(iconView)

            progressBar.setVisible(tab.data is TransactionState)
            (tab.data as? TransactionState)?.let { progressBar.setProgressWithAnimation(it.progress(), duration = 200) }
        }
    }

    private inner class BubbleDragListener : DraggableLayout.OnDragListener {
        override fun onDrag(originX: Float, originY: Float, x: Float, y: Float) {}

        override fun onDragEnd() {
            with(binding) {
                floatBubble.setBackgroundResource(if (binding.floatBubble.isNearestLeft) R.drawable.bg_round_right_12dp else R.drawable.bg_round_left_12dp)
            }
        }

        override fun onDragStart() {
            binding.floatBubble.setBackgroundResource(R.drawable.bg_round_12dp)
        }
    }
}