package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletCoinItemBinding
import io.outblock.lilico.page.token.detail.TokenDetailActivity
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.utils.formatPrice
import java.math.RoundingMode

class WalletCoinItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletCoinItemModel> {

    private val binding by lazy { LayoutWalletCoinItemBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletCoinItemModel) {
        with(binding) {
            Glide.with(coinIcon).load(model.coin.icon).into(coinIcon)
            coinName.text = model.coin.name
            if (model.isHideBalance) {
                coinBalance.text = "**** ${model.coin.symbol.uppercase()}"
                coinBalancePrice.text = "****"
            } else {
                coinBalance.text = "${model.balance.formatPrice(roundingMode = RoundingMode.HALF_UP)} ${model.coin.symbol.uppercase()}"
                coinBalancePrice.text = "$${(model.balance * model.coinRate).formatPrice()}"
            }
            coinPrice.text = "$" + model.coinRate.formatPrice()
            view.setOnClickListener { TokenDetailActivity.launch(view.context, model.coin) }
        }
    }
}