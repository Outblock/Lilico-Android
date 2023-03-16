package io.outblock.lilico.page.staking.list.presenter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemStakeListBinding
import io.outblock.lilico.manager.staking.*
import io.outblock.lilico.page.staking.amount.StakingAmountActivity
import io.outblock.lilico.page.staking.detail.StakingDetailActivity
import io.outblock.lilico.page.staking.list.model.StakingListItemModel
import io.outblock.lilico.utils.formatNum

class StakingListItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<StakingListItemModel> {

    private val binding by lazy { ItemStakeListBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: StakingListItemModel) {
        with(binding) {
            claimButton.setOnClickListener { StakingAmountActivity.launch(view.context, model.provider) }
            Glide.with(providerIcon).load(model.provider.icon).placeholder(R.drawable.placeholder).into(providerIcon)
            providerName.text = model.provider.name
            providerRate.text = (model.provider.rate() * 100).formatNum(2) + "%"
            amountView.text = model.stakingNode.stakingCount().formatNum(3)
            rewardView.text = model.stakingNode.tokensRewarded.formatNum(3)
        }

        view.setOnClickListener { StakingDetailActivity.launch(view.context, model.provider) }
    }

}