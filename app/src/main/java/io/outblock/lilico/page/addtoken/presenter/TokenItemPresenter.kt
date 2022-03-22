package io.outblock.lilico.page.addtoken.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemTokenListBinding
import io.outblock.lilico.page.addtoken.AddTokenConfirmDialog
import io.outblock.lilico.page.addtoken.model.TokenItem
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity

class TokenItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<TokenItem> {

    private val binding by lazy { ItemTokenListBinding.bind(view) }

    override fun bind(model: TokenItem) {
        with(binding) {
            nameView.text = model.coin.name
            symbolView.text = model.coin.symbol.uppercase()
            Glide.with(iconView).load(model.coin.icon).into(iconView)
            stateButton.setOnClickListener {
                if (model.isNormalState()) {
                    AddTokenConfirmDialog.show((findActivity(view) as FragmentActivity).supportFragmentManager, model.coin)
                }
            }
            progressBar.setVisible(model.isAdding == true)
            stateButton.setVisible(model.isAdding != true)
            stateButton.setImageResource(if (model.isNormalState()) R.drawable.ic_add_circle else R.drawable.ic_check_round)
        }
    }
}