package io.outblock.lilico.page.window.bubble

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.outblock.lilico.databinding.WindowBubbleBinding
import io.outblock.lilico.page.window.bubble.model.BubbleModel
import io.outblock.lilico.page.window.bubble.model.FloatTabsModel
import io.outblock.lilico.page.window.bubble.presenter.BubblePresenter
import io.outblock.lilico.page.window.bubble.presenter.FloatTabsPresenter

class Bubble : FrameLayout {
    private var binding: WindowBubbleBinding = WindowBubbleBinding.inflate(LayoutInflater.from(context))
    private var floatTabsPresenter: FloatTabsPresenter
    private var bubblePresenter: BubblePresenter

    private val viewModel by lazy { BubbleViewModel() }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        addView(binding.root)

        floatTabsPresenter = FloatTabsPresenter(binding, viewModel)
        bubblePresenter = BubblePresenter(binding, viewModel)

        with(viewModel) {
            onTabChange = {
                bubblePresenter.bind(BubbleModel(onTabChange = true))
                floatTabsPresenter.bind(FloatTabsModel(onTabChange = true))
            }
        }
    }

    fun viewModel() = viewModel

    fun binding() = binding
}