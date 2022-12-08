package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletCoinItemBinding
import io.outblock.lilico.page.token.detail.TokenDetailActivity
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.formatNum
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
                coinBalance.text = "${model.balance.formatNum(roundingMode = RoundingMode.HALF_UP)} ${model.coin.symbol.uppercase()}"
                coinBalancePrice.text = (model.balance * model.coinRate).formatPrice(includeSymbol = true)
            }
            coinPrice.text = model.coinRate.formatPrice(includeSymbol = true)
            bindStaking(model)
            view.setOnClickListener { TokenDetailActivity.launch(view.context, model.coin) }
        }
    }

    private fun bindStaking(model: WalletCoinItemModel) {
        if (!model.coin.isFlowCoin() || !model.isStaked) {
            setStakingVisible(false)
            return
        }
        setStakingVisible(true)
        binding.stakingAmount.text = view.context.getString(R.string.flow_num, model.stakeAmount.formatNum(3))
        binding.stakingAmountPrice.text = (model.stakeAmount * model.coinRate).formatPrice(includeSymbol = true)
    }

    private fun setStakingVisible(isVisible: Boolean) {
        with(binding) {
            stakingLine.setVisible(isVisible)
            stakingAmount.setVisible(isVisible)
            stakingName.setVisible(isVisible)
            stakingPoint.setVisible(isVisible)
            stakingAmountPrice.setVisible(isVisible)
        }
    }
}