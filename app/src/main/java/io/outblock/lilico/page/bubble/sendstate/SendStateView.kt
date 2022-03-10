package io.outblock.lilico.page.bubble.sendstate

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import io.outblock.lilico.databinding.WindowSendStateBubbleContentBinding

class SendStateView : MaterialCardView {
    private val binding = WindowSendStateBubbleContentBinding.inflate(LayoutInflater.from(context))

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        addView(binding.root)

        with(binding) {
            progressBar.progress = 0.4f
        }
    }
}