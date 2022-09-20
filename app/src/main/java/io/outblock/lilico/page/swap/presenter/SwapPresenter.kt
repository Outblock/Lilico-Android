package io.outblock.lilico.page.swap.presenter

import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivitySwapBinding
import io.outblock.lilico.page.swap.*
import io.outblock.lilico.page.swap.dialog.confirm.SwapTokenConfirmDialog
import io.outblock.lilico.page.swap.dialog.select.SelectTokenDialog
import io.outblock.lilico.page.swap.model.SwapModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.uiScope

class SwapPresenter(
    private val binding: ActivitySwapBinding,
    private val activity: SwapActivity,
) : BasePresenter<SwapModel> {

    init {
        setupToolbar()
        binding.bindInputListener()
        with(binding) {
            root.addStatusBarTopPadding()
            root.addNavigationBarBottomPadding()
            maxButton.setOnClickListener { setMaxAmount() }
            switchButton.setOnClickListener { viewModel().switchCoin() }
            swapButton.setOnClickListener { SwapTokenConfirmDialog.show(activity.supportFragmentManager) }
            fromButton.setOnClickListener { showSelectTokenDialog(true) }
            toButton.setOnClickListener { showSelectTokenDialog(false) }
        }
    }

    override fun bind(model: SwapModel) {
        model.fromCoin?.let { binding.updateFromCoin(it) }
        model.toCoin?.let { binding.updateToCoin(it) }
        model.onBalanceUpdate?.let { binding.onBalanceUpdate() }
        model.onCoinRateUpdate?.let { binding.onCoinRateUpdate() }
        model.onEstimateFromUpdate?.let { binding.updateFromAmount(it) }
        model.onEstimateToUpdate?.let { binding.updateToAmount(it) }
        model.onEstimateLoading?.let { binding.updateProgressState(it) }
        model.estimateData?.let { binding.updateEstimate(it) }
    }

    private fun showSelectTokenDialog(isFrom: Boolean) {
        uiScope {
            val viewModel = binding.viewModel()
            val symbol = if (isFrom) viewModel.fromCoin()?.symbol else viewModel.toCoin()?.symbol
            SelectTokenDialog().show(symbol, activity.supportFragmentManager)?.let {
                if (isFrom) viewModel.updateFromCoin(it) else viewModel.updateToCoin(it)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.swap.res2String()
    }
}