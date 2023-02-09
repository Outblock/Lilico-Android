package io.outblock.lilico.page.staking.detail.presenter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.format.DateUtils
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityStakingDetailBinding
import io.outblock.lilico.databinding.LayoutStakingDetailStateBinding
import io.outblock.lilico.manager.staking.*
import io.outblock.lilico.page.staking.amount.StakingAmountActivity
import io.outblock.lilico.page.staking.detail.StakingDetailActivity
import io.outblock.lilico.page.staking.detail.StakingDetailViewModel
import io.outblock.lilico.page.staking.detail.model.StakingDetailModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.formatNum
import io.outblock.lilico.utils.formatPrice
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class StakingDetailPresenter(
    private val binding: ActivityStakingDetailBinding,
    private val provider: StakingProvider,
    private val activity: StakingDetailActivity,
) : BasePresenter<StakingDetailModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[StakingDetailViewModel::class.java] }

    init {
        with(binding) {
            root.addStatusBarTopPadding()
            root.addNavigationBarBottomPadding()
            stakeButton.setOnClickListener { StakingAmountActivity.launch(activity, provider) }
            unstakeButton.setOnClickListener { StakingAmountActivity.launch(activity, provider, isUnstake = true) }
            header.claimButton.setOnClickListener { viewModel.claimRewards(provider) }
            header.restakeButton.setOnClickListener { viewModel.restakeRewards(provider) }
            unstakedWrapper.claimButton.setOnClickListener { viewModel.claimUnstaked(provider) }
            unstakedWrapper.restakeButton.setOnClickListener { viewModel.restakeUnstaked(provider) }
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
        setupState(model)
        setupEpoch(model)
        setupRewards(model)
        setupItems(model)
    }

    private fun setupState(model: StakingDetailModel) {
        binding.unstakedWrapper.root.setVisible(model.stakingNode.tokensUnstaked > 0)
        binding.unstakedWrapper.unstakedAmountView.text = model.stakingNode.tokensUnstaked.formatNum(3)

        fun LayoutStakingDetailStateBinding.setup(
            titleString: Int,
            isInProgress: Boolean,
            amount: Float,
        ) {
            title.setText(titleString)
            amountView.text = amount.formatNum(3)
            val color = if (isInProgress) R.color.mango1.res2color() else R.color.success2.res2color()
            clockIcon.imageTintList = ColorStateList.valueOf(color)
            stateText.setTextColor(color)
            root.setVisible(amount > 0)
        }

        binding.commitWrapper.setup(R.string.commit_to_stake, isInProgress = false, model.stakingNode.tokensCommitted)
        binding.unstakingWrapper.setup(R.string.unstaking, isInProgress = true, model.stakingNode.tokensUnstaking)
        binding.requestUnstakeWrapper.setup(R.string.request_to_unstake, isInProgress = true, model.stakingNode.tokensRequestedToUnstake)
    }

    @SuppressLint("SetTextI18n")
    private fun setupItems(model: StakingDetailModel) {
        with(binding.itemsWrapper) {
            val stakingNode = model.stakingNode
            unstakedAmountView.text = activity.getString(R.string.flow_num, stakingNode.tokensUnstaked.formatNum(3))
            unstakingAmountView.text = activity.getString(R.string.flow_num, stakingNode.tokensUnstaking.formatNum(3))
            committedAmountView.text = activity.getString(R.string.flow_num, stakingNode.tokensCommitted.formatNum(3))
            requestedToUnstakeAmountView.text = activity.getString(R.string.flow_num, stakingNode.tokensRequestedToUnstake.formatNum(3))
            aprView.text = (provider.rate() * 100).formatNum(2) + "%"
        }
    }

    private fun setupHeader(model: StakingDetailModel) {
        with(binding.header) {
            val stakingCount = model.stakingNode.stakingCount()
            amountPriceView.text = (stakingCount * model.coinRate).formatPrice(3, includeSymbol = true)
            amountPriceCurrencyView.text = model.currency.name
            stakeAmountView.text = stakingCount.formatNum(3)
            availableAmountView.text = model.balance.formatNum(3)

            rewardsAmountView.text = model.stakingNode.tokensRewarded.formatNum(3)
        }
    }

    private fun setupEpoch(model: StakingDetailModel) {
        with(binding.epochWrapper) {
            val startTime = stakingEpochStartTime()
            epochStartView.text = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startTime)
            epochEndView.text = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(stakingEpochEndTime())

            val diffTime = (System.currentTimeMillis() - startTime) * 1.0f / DateUtils.DAY_IN_MILLIS
            stepProgressBar.setup(7, min(7, ceil(diffTime).toInt()))
        }
    }

    private fun setupRewards(model: StakingDetailModel) {
        with(binding.rewardWrapper) {
            val dayApy = StakingManager.apy() / 365.0f
            dailyView.text = (model.stakingNode.stakingCount() * dayApy * model.coinRate).formatPrice(3, includeSymbol = true)
            dailyCurrencyName.text = model.currency.name
            dailyFlowCount.text = activity.getString(R.string.flow_num, (model.stakingNode.stakingCount() * dayApy).formatNum(3))

            val monthApy = StakingManager.apy() / 12
            monthlyView.text = (model.stakingNode.stakingCount() * monthApy * model.coinRate).formatPrice(3, includeSymbol = true)
            monthlyCurrencyName.text = model.currency.name
            monthlyFlowCount.text = activity.getString(R.string.flow_num, (model.stakingNode.stakingCount() * monthApy).formatNum(3))
        }
    }

}