package io.outblock.lilico.page.wallet.presenter

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletCoinItemBinding
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.utils.formatBalance
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP

class WalletCoinItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletCoinItemModel> {

    private val binding by lazy { LayoutWalletCoinItemBinding.bind(view) }

    override fun bind(model: WalletCoinItemModel) {
        with(binding) {
            // TODO bind data by coin type and server data
            val currencyPrice = BigDecimal(9.08)
            val flowDecimal = BigDecimal(10).pow(8)
            val balance = BigDecimal(model.balance).divide(flowDecimal)
            val currencyBalance = balance.multiply(currencyPrice)

            coinIcon.setImageResource(R.drawable.ic_coin_flow)
            coinName.text = "Flow"
            coinBalance.text = "${model.balance.formatBalance()} Flow"
            coinPrice.text = "$9.08"
            coinBalancePrice.text = "$${currencyBalance.setScale(3, ROUND_HALF_UP)}"
        }
    }
}