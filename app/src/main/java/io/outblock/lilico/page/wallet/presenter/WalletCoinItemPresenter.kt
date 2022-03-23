package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletCoinItemBinding
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.utils.formatPrice

class WalletCoinItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletCoinItemModel> {

    private val binding by lazy { LayoutWalletCoinItemBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletCoinItemModel) {
        with(binding) {
            Glide.with(coinIcon).load(model.coin.icon).into(coinIcon)
            coinName.text = model.coin.name
            coinBalance.text = "${model.balance} ${model.coin.symbol.uppercase()}"
            coinPrice.text = "$" + model.coinRate.formatPrice()
            coinBalancePrice.text = "$${(model.balance * model.coinRate).formatPrice()}"
        }
    }
}