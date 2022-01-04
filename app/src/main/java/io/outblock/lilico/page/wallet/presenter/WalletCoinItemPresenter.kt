package io.outblock.lilico.page.wallet.presenter

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletCoinItemBinding
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel

class WalletCoinItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletCoinItemModel> {

    private val binding by lazy { LayoutWalletCoinItemBinding.bind(view) }

    override fun bind(model: WalletCoinItemModel) {
        with(binding) {
            // TODO bind data by coin type and server data
            coinIcon.setImageResource(R.drawable.ic_coin_flow)
            coinName.text = "Flow"
            coinBalance.text = "${model.balance} Flow"
            coinPrice.text = "$9.8"
            coinBalancePrice.text = "$${model.balance * 9.8f}"
        }
    }
}