package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter

import android.annotation.SuppressLint
import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAccessibleCoinBinding
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.CoinData
import io.outblock.lilico.utils.formatNum
import io.outblock.lilico.utils.loadAvatar
import java.math.RoundingMode

/**
 * Created by Mengxy on 8/15/23.
 */
class AccessibleCoinPresenter(private val view: View): BaseViewHolder(view), BasePresenter<CoinData> {

    private val binding by lazy { ItemAccessibleCoinBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: CoinData) {
        with(binding) {
            iconView.loadAvatar(model.icon)
            titleView.text = model.name
            coinBalanceView.text = "${model.balance.formatNum(roundingMode = RoundingMode.HALF_UP)} " + model.symbol.uppercase()
        }
    }
}