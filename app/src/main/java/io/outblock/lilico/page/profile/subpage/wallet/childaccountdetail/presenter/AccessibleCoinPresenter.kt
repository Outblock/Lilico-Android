package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter

import android.annotation.SuppressLint
import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAccessibleCoinBinding
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.utils.formatNum
import io.outblock.lilico.utils.loadAvatar
import java.math.RoundingMode

/**
 * Created by Mengxy on 8/15/23.
 */
class AccessibleCoinPresenter(private val view: View): BaseViewHolder(view), BasePresenter<WalletCoinItemModel> {

    private val binding by lazy { ItemAccessibleCoinBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletCoinItemModel) {
        with(binding) {
            iconView.loadAvatar(model.coin.icon)
            titleView.text = model.coin.name
            coinBalanceView.text = "${model.balance.formatNum(roundingMode = RoundingMode.HALF_UP)} " + model.coin.symbol.uppercase()
        }
    }
}