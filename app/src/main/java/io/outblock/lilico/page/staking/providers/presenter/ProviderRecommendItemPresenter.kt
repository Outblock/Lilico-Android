package io.outblock.lilico.page.staking.providers.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemStakeProviderRecommendBinding
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.manager.staking.isLilico
import io.outblock.lilico.page.staking.amount.StakingAmountActivity
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.formatNum

class ProviderRecommendItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<StakingProvider> {

    private val binding by lazy { ItemStakeProviderRecommendBinding.bind(view) }

    override fun bind(model: StakingProvider) {
        with(binding) {
            Glide.with(iconView).load(model.icon).placeholder(R.drawable.placeholder).into(iconView)
            titleView.text = model.name
            descView.text = model.description
            rateTitle.text = if (model.isLilico()) (StakingManager.apy() * 100).formatNum(2) else "8%"
            rateDesc.text = R.string.stake.res2String()
        }

        view.setOnClickListener { StakingAmountActivity.launch(view.context, model) }
    }

}