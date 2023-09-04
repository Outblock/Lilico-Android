package io.outblock.lilico.page.swap.dialog.select

import android.content.res.ColorStateList
import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemTokenListBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible

class TokenItemPresenter(
    private val view: View,
    private val selectedCoin: String? = null,
    private var disableCoin: String? = null,
    private val callback: (FlowCoin) -> Unit
) : BaseViewHolder(view), BasePresenter<FlowCoin> {

    private val binding by lazy { ItemTokenListBinding.bind(view) }

    override fun bind(model: FlowCoin) {
        with(binding) {
            nameView.text = model.name
            symbolView.text = model.symbol.uppercase()
            Glide.with(iconView).load(model.icon).into(iconView)
            stateButton.setVisible(false)
        }

        val backgroundColor = when (model.symbol) {
            selectedCoin -> R.color.swap_token_selected.res2color()
            disableCoin -> R.color.transparent.res2color()
            else -> R.color.background.res2color()
        }

        view.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        view.setOnClickListener {
            if (disableCoin != model.symbol) {
                callback.invoke(model)
            }
        }
    }
}