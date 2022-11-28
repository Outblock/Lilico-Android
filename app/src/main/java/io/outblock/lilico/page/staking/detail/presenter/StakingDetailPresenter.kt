package io.outblock.lilico.page.staking.detail.presenter

import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityStakingDetailBinding
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.manager.staking.stakingCount
import io.outblock.lilico.page.staking.detail.StakingDetailActivity
import io.outblock.lilico.page.staking.detail.model.StakingDetailModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.formatNum
import io.outblock.lilico.utils.formatPrice

class StakingDetailPresenter(
    private val binding: ActivityStakingDetailBinding,
    private val provider: StakingProvider,
    private val activity: StakingDetailActivity,
) : BasePresenter<StakingDetailModel> {

    init {
        with(binding) {
            root.addStatusBarTopPadding()
            root.addNavigationBarBottomPadding()
        }
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.staking_detail.res2String()
    }

    override fun bind(model: StakingDetailModel) {
        setupHeader(model)
    }

    private fun setupHeader(model: StakingDetailModel) {
        with(binding.header) {
            val stakingCount = model.stakingNode.stakingCount()
            amountPriceView.text = (stakingCount * model.coinRate).formatPrice(3, includeSymbol = true)
            amountPriceCurrencyView.text = model.currency.name
            stakeAmountView.text = stakingCount.formatNum(3)
            availableAmountView.text = (model.balance - stakingCount).formatNum(3)

            rewardsAmountView.text = model.stakingNode.tokensRewarded.formatNum(3)
        }
    }
}