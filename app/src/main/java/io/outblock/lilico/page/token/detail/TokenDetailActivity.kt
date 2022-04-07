package io.outblock.lilico.page.token.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityTokenDetailBinding
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.page.token.detail.model.TokenDetailModel
import io.outblock.lilico.page.token.detail.presenter.TokenDetailPresenter

class TokenDetailActivity : BaseActivity() {

    private val coin by lazy { intent.getParcelableExtra<FlowCoin>(EXTRA_COIN)!! }

    private lateinit var binding: ActivityTokenDetailBinding
    private lateinit var presenter: TokenDetailPresenter
    private lateinit var viewModel: TokenDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokenDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = TokenDetailPresenter(this, binding, coin)
        viewModel = ViewModelProvider(this)[TokenDetailViewModel::class.java].apply {
            setCoin(coin)
            balanceAmountLiveData.observe(this@TokenDetailActivity) { presenter.bind(TokenDetailModel(balanceAmount = it)) }
            balancePriceLiveData.observe(this@TokenDetailActivity) { presenter.bind(TokenDetailModel(balancePrice = it)) }
            charDataLiveData.observe(this@TokenDetailActivity) { presenter.bind(TokenDetailModel(chartData = it)) }
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