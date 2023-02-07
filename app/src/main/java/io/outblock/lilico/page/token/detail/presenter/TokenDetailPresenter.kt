package io.outblock.lilico.page.token.detail.presenter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityTokenDetailBinding
import io.outblock.lilico.manager.app.isMainnet
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.staking.STAKING_DEFAULT_NORMAL_APY
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.manager.staking.isLilico
import io.outblock.lilico.manager.staking.stakingCount
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.profile.subpage.currency.model.selectedCurrency
import io.outblock.lilico.page.receive.ReceiveActivity
import io.outblock.lilico.page.send.transaction.TransactionSendActivity
import io.outblock.lilico.page.staking.openStakingPage
import io.outblock.lilico.page.token.detail.TokenDetailViewModel
import io.outblock.lilico.page.token.detail.model.TokenDetailModel
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.formatNum
import io.outblock.lilico.utils.formatPrice

class TokenDetailPresenter(
    private val activity: AppCompatActivity,
    private val binding: ActivityTokenDetailBinding,
    private val coin: FlowCoin,
) : BasePresenter<TokenDetailModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[TokenDetailViewModel::class.java] }

    init {
        setupToolbar()
        with(binding) {
            root.addStatusBarTopPadding()
            root.addNavigationBarBottomPadding()
            nameView.text = coin.name
            coinTypeView.text = coin.symbol.uppercase()
            Glide.with(iconView).load(coin.icon).into(iconView)
            nameWrapper.setOnClickListener { openBrowser(activity, coin.website) }
            getMoreWrapper.setOnClickListener { }
            sendButton.setOnClickListener { TransactionSendActivity.launch(activity, coinSymbol = coin.symbol) }
            receiveButton.setOnClickListener { ReceiveActivity.launch(activity) }
        }

        if (!coin.isFlowCoin() && coin.symbol != FlowCoin.SYMBOL_FUSD) {
            binding.getMoreWrapper.setVisible(false)
            binding.chartWrapper.root.setVisible(false)
        }

        if (!StakingManager.isStaked() && coin.isFlowCoin()) {
            binding.stakingBanner.root.setVisible(true)
            binding.getMoreWrapper.setVisible(false)
            binding.stakingBanner.root.setOnClickListener { openStakingPage(activity) }
        }

        if (StakingManager.isStaked() && coin.isFlowCoin()) {
            binding.getMoreWrapper.setVisible(false)
            setupStakingRewards()
        }

        if (!isMainnet()) {
            binding.getMoreWrapper.setOnClickListener {
                openBrowser(
                    activity,
                    if (isTestnet()) "https://testnet-faucet.onflow.org/fund-account" else "https://sandboxnet-faucet.flow.com/"
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bind(model: TokenDetailModel) {
        model.balanceAmount?.let { binding.balanceAmountView.text = it.formatNum() }
        model.balancePrice?.let { binding.balancePriceView.text = "${it.formatPrice(includeSymbol = true)} ${selectedCurrency().name}" }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = ""
    }

    private fun setupStakingRewards() {
        with(binding.stakingRewardWrapper) {
            val currency = selectedCurrency()
            val coinRate = CoinRateManager.coinRate(FlowCoin.SYMBOL_FLOW) ?: 0f
            val stakingCount = StakingManager.stakingCount()

            val dayRewards =
                StakingManager.stakingInfo().nodes.sumOf { it.stakingCount() * (if (it.isLilico()) StakingManager.apy() else STAKING_DEFAULT_NORMAL_APY).toDouble() }
                    .toFloat() / 365.0f
            stakingCountView.text = activity.getString(R.string.flow_num, stakingCount.formatNum(3))
            dailyView.text = (dayRewards * coinRate).formatPrice(3, includeSymbol = true)
            dailyCurrencyName.text = currency.name
            dailyFlowCount.text = activity.getString(R.string.flow_num, dayRewards.formatNum(3))

            val monthRewards = dayRewards * 30
            monthlyView.text = (monthRewards * coinRate).formatPrice(3, includeSymbol = true)
            monthlyCurrencyName.text = currency.name
            monthlyFlowCount.text =
                activity.getString(R.string.flow_num, monthRewards.formatNum(3))
            root.setOnClickListener { openStakingPage(activity) }
            root.setVisible(true)
        }
    }
}