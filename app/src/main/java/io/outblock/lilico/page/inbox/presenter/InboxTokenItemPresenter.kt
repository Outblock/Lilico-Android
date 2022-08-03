package io.outblock.lilico.page.inbox.presenter

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemInboxTokenBinding
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.network.model.InboxToken
import io.outblock.lilico.page.inbox.InboxViewModel
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.formatPrice

class InboxTokenItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<InboxToken> {
    private val binding by lazy { ItemInboxTokenBinding.bind(view) }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[InboxViewModel::class.java] }

    @SuppressLint("SetTextI18n")
    override fun bind(model: InboxToken) {
        with(binding) {
            val coin = FlowCoinListManager.coinList().firstOrNull { it.address() == model.coinAddress } ?: return
            Glide.with(coinIconView).load(coin.icon).into(coinIconView)
            amountView.text = "${model.amount.formatPrice()} ${coin.symbol.uppercase()}"

            val marketValue = model.marketValue
            priceCountView.text = if (marketValue == null) "$0" else "$ ${marketValue.formatPrice()}"

            claimButton.setOnClickListener { viewModel.claimToken(model) }
        }
    }
}