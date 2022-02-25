package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletCoinItemBinding
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.utils.Coin
import io.outblock.lilico.utils.formatBalance
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.name

class WalletCoinItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletCoinItemModel> {

    private val binding by lazy { LayoutWalletCoinItemBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletCoinItemModel) {
        with(binding) {
            coinIcon.setImageResource(R.drawable.ic_coin_flow)
            coinName.text = Coin.FLOW.name()
            coinBalance.text = "${model.balance.formatBalance().formatPrice()} Flow"
            coinPrice.text = model.coinRate.formatPrice()
            coinBalancePrice.text = "$${(model.balance.formatBalance() * model.coinRate).formatPrice()}"
        }
    }
}