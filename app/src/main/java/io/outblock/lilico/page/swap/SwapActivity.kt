package io.outblock.lilico.page.swap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySwapBinding
import io.outblock.lilico.page.swap.model.SwapModel
import io.outblock.lilico.page.swap.presenter.SwapPresenter
import io.outblock.lilico.utils.isNightMode

class SwapActivity : BaseActivity() {

    private lateinit var binding: ActivitySwapBinding
    private lateinit var viewModel: SwapViewModel
    private lateinit var presenter: SwapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()
        UltimateBarX.with(this).fitWindow(true).light(!isNightMode(this)).applyNavigationBar()

        presenter = SwapPresenter(binding, this)
        viewModel = ViewModelProvider(this)[SwapViewModel::class.java].apply {
            fromCoinLiveData.observe(this@SwapActivity) { presenter.bind(SwapModel(fromCoin = it)) }
            toCoinLiveData.observe(this@SwapActivity) { presenter.bind(SwapModel(toCoin = it)) }
            onBalanceUpdate.observe(this@SwapActivity) { presenter.bind(SwapModel(onBalanceUpdate = it)) }
            onCoinRateUpdate.observe(this@SwapActivity) { presenter.bind(SwapModel(onCoinRateUpdate = it)) }
            onEstimateFromUpdate.observe(this@SwapActivity) { presenter.bind(SwapModel(onEstimateFromUpdate = it)) }
            onEstimateToUpdate.observe(this@SwapActivity) { presenter.bind(SwapModel(onEstimateToUpdate = it)) }
            onEstimateLoading.observe(this@SwapActivity) { presenter.bind(SwapModel(onEstimateLoading = it)) }
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
        fun launch(context: Context) {
            context.startActivity(Intent(context, SwapActivity::class.java))
        }
    }
}