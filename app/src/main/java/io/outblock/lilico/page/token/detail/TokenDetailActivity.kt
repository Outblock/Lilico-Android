package io.outblock.lilico.page.token.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityTokenDetailBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.page.token.detail.model.TokenDetailActivitiesModel
import io.outblock.lilico.page.token.detail.model.TokenDetailChartModel
import io.outblock.lilico.page.token.detail.model.TokenDetailModel
import io.outblock.lilico.page.token.detail.presenter.TokenDetailActivitiesPresenter
import io.outblock.lilico.page.token.detail.presenter.TokenDetailChartPresenter
import io.outblock.lilico.page.token.detail.presenter.TokenDetailPresenter
import io.outblock.lilico.utils.isNightMode

class TokenDetailActivity : BaseActivity() {

    private val coin by lazy { intent.getParcelableExtra<FlowCoin>(EXTRA_COIN)!! }

    private lateinit var binding: ActivityTokenDetailBinding
    private lateinit var presenter: TokenDetailPresenter
    private lateinit var chartPresenter: TokenDetailChartPresenter
    private lateinit var activitiesPresenter: TokenDetailActivitiesPresenter
    private lateinit var viewModel: TokenDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokenDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()
        UltimateBarX.with(this).fitWindow(true).light(!isNightMode(this)).applyNavigationBar()

        presenter = TokenDetailPresenter(this, binding, coin)
        chartPresenter = TokenDetailChartPresenter(this, binding.chartWrapper)
        activitiesPresenter = TokenDetailActivitiesPresenter(this, binding.activitiesWrapper, coin)

        viewModel = ViewModelProvider(this)[TokenDetailViewModel::class.java].apply {
            setCoin(coin)
            balanceAmountLiveData.observe(this@TokenDetailActivity) { presenter.bind(TokenDetailModel(balanceAmount = it)) }
            balancePriceLiveData.observe(this@TokenDetailActivity) { presenter.bind(TokenDetailModel(balancePrice = it)) }
            summaryLiveData.observe(this@TokenDetailActivity) { chartPresenter.bind(TokenDetailChartModel(summary = it)) }
            chartDataLiveData.observe(this@TokenDetailActivity) { chartPresenter.bind(TokenDetailChartModel(chartData = it)) }
            transactionListLiveData.observe(this@TokenDetailActivity) { activitiesPresenter.bind(TokenDetailActivitiesModel(recordList = it)) }
            load()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        private const val EXTRA_COIN = "EXTRA_COIN"
        fun launch(context: Context, coin: FlowCoin) {
            context.startActivity(Intent(context, TokenDetailActivity::class.java).apply {
                putExtra(EXTRA_COIN, coin)
            })
        }
    }
}