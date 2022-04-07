package io.outblock.lilico.page.token.detail.presenter

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityTokenDetailBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.page.token.detail.Quote
import io.outblock.lilico.page.token.detail.TokenDetailViewModel
import io.outblock.lilico.page.token.detail.model.TokenDetailModel
import io.outblock.lilico.utils.extensions.res2color
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
            nameView.text = coin.name
            coinTypeView.text = coin.symbol.uppercase()
            Glide.with(iconView).load(coin.icon).into(iconView)
            chartWrapper.chartPeriodTabs.setOnTabClickListener { viewModel.changePeriod(it) }
            getMoreWrapper.setOnClickListener { }
            sendButton.setOnClickListener { }
            receiveButton.setOnClickListener { }
        }
    }

    override fun bind(model: TokenDetailModel) {
        model.balanceAmount?.let { binding.balanceAmountView.text = it.formatPrice() }
        model.balancePrice?.let { binding.balancePriceView.text = activity.getString(R.string.usd_balance, it.formatPrice()) }
        model.chartData?.let { updateChartData(it) }
    }

    private fun updateChartData(quotes: List<Quote>) {

    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = ""
    }
}