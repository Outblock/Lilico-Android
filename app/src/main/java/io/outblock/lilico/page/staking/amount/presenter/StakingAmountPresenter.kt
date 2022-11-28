package io.outblock.lilico.page.staking.amount.presenter

import android.annotation.SuppressLint
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.addNavigationBarBottomPadding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityStakingAmountBinding
import io.outblock.lilico.manager.price.CurrencyManager
import io.outblock.lilico.manager.staking.*
import io.outblock.lilico.page.profile.subpage.currency.model.findCurrencyFromFlag
import io.outblock.lilico.page.staking.amount.StakingAmountActivity
import io.outblock.lilico.page.staking.amount.StakingAmountViewModel
import io.outblock.lilico.page.staking.amount.dialog.StakingAmountConfirmDialog
import io.outblock.lilico.page.staking.amount.dialog.StakingAmountConfirmModel
import io.outblock.lilico.page.staking.amount.model.StakingAmountModel
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.hideKeyboard
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.toSafeFloat

@SuppressLint("SetTextI18n")
class StakingAmountPresenter(
    private val binding: ActivityStakingAmountBinding,
    private val provider: StakingProvider,
    private val activity: StakingAmountActivity,
) : BasePresenter<StakingAmountModel> {
    private val viewModel by lazy { ViewModelProvider(activity)[StakingAmountViewModel::class.java] }

    private val currency by lazy { findCurrencyFromFlag(CurrencyManager.currencyFlag()) }

    init {
        with(binding) {
            root.addStatusBarTopPadding()
            root.addNavigationBarBottomPadding()
            rateView.text = "${((if (provider.isLilico()) StakingManager.apy() else STAKING_DEFAULT_NORMAL_APY) * 100).format(2)}%"
            currencyName.text = currency.name
            rewardPriceCurrencyView.text = currency.name
            onAmountChange()
            amountPercent30.setOnClickListener { updateAmountByPercent(0.3f) }
            amountPercent50.setOnClickListener { updateAmountByPercent(0.5f) }
            amountPercentMax.setOnClickListener { updateAmountByPercent(1.0f) }
            button.setOnClickListener {
                StakingAmountConfirmDialog.show(
                    activity, StakingAmountConfirmModel(
                        amount = amount(),
                        coinRate = viewModel.coinRate(),
                        currency = currency,
                        rate = provider.rate(),
                        rewardCoin = amount() * StakingManager.apy(),
                        rewardUsd = (amount() * StakingManager.apy() * viewModel.coinRate()),
                        provider = provider,
                    )
                )
            }
        }
        with(binding.inputView) {
            doOnTextChanged { _, _, _, _ ->
                onAmountChange()
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                }
                return@setOnEditorActionListener false
            }
        }
        setupToolbar()
    }

    override fun bind(model: StakingAmountModel) {
        model.balance?.let { onUpdateBalance(it) }
        model.processingState?.let { onProcessingUpdate(it) }
    }

    private fun onProcessingUpdate(it: Boolean) {
        binding.button.setProgressVisible(false)
        if (it) {
            activity.finish()
        } else {
            toast(R.string.claim_failed)
            onAmountChange()
        }
    }

    private fun onUpdateBalance(it: Float) {
        binding.balanceView.text = activity.getString(R.string.flow_available_num, it.formatNum(2))
    }

    private fun onAmountChange() {
        with(binding) {
            priceView.text = (amount() * viewModel.coinRate()).formatPrice(includeSymbol = true)
            rewardCoinView.text = "${(amount() * StakingManager.apy()).formatNum(digits = 2)} " + R.string.flow_coin_name.res2String()
            rewardPriceView.text = "≈ ${(amount() * StakingManager.apy() * viewModel.coinRate()).formatPrice(digits = 2, includeSymbol = true)} "
            availableCheck()
        }
    }

    private fun availableCheck() {
        val amount = amount()
        if (amount == 0.0f) {
            binding.button.setText(R.string.next)
            binding.button.isEnabled = false
        } else if (amount > balance()) {
            binding.button.setText(R.string.insufficient_balance)
            binding.button.isEnabled = false
        } else {
            binding.button.setText(R.string.next)
            binding.button.isEnabled = true
        }
    }

    private fun updateAmountByPercent(percent: Float) {
        val text = ((viewModel.balanceLiveData.value ?: 0.0f) * percent).formatNum(2)
        binding.inputView.setText(text)
        binding.inputView.setSelection(text.length)
    }

    private fun balance() = viewModel.balanceLiveData.value ?: 0.0f

    private fun amount() = binding.inputView.text.toString().toSafeFloat()

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.stake_amount.res2String()
    }
}