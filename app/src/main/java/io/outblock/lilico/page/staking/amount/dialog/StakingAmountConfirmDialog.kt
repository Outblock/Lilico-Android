package io.outblock.lilico.page.staking.amount.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogStakingAmountConfirmBinding
import io.outblock.lilico.manager.app.chainNetWorkString
import io.outblock.lilico.manager.flowjvm.*
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.widgets.ButtonState

class StakingAmountConfirmDialog : BottomSheetDialogFragment() {

    private val data by lazy { arguments?.getParcelable<StakingAmountConfirmModel>(DATA)!! }
    private lateinit var binding: DialogStakingAmountConfirmBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogStakingAmountConfirmBinding.inflate(inflater)
        return binding.rootView
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            Glide.with(providerIcon).load(data.provider.icon).placeholder(R.drawable.placeholder).into(providerIcon)
            providerName.text = data.provider.name
            amountView.text = data.amount.format(3)
            amoundPriceView.text = (data.amount * data.coinRate).formatPrice(3, includeSymbol = true)
            amoundPriceCurrencyView.text = data.currency.name

            rateView.text = (data.rate * 100).format(2) + "%"
            rewardCoinView.text = "${(data.rewardCoin).formatNum(digits = 2)} " + R.string.flow_coin_name.res2String()
            rewardPriceView.text = "≈ ${(data.rewardUsd).formatPrice(digits = 2, includeSymbol = true)}"
            rewardPriceCurrencyView.text = data.currency.name

            sendButton.setOnProcessing { sendStake() }

            closeButton.setOnClickListener { dismiss() }
        }
    }

    private fun sendStake() {
        ioScope {
            val isStakingEnable = checkStakingEnabled()
            if (!isStakingEnable) {
                toast(msg = getString(R.string.staking_not_enabled, chainNetWorkString()))
                uiScope { safeRun { dismiss() } }
                return@ioScope
            }
            val isSuccess = stake()
            safeRun {
                if (isSuccess) {
                    requireActivity().finish()
                } else {
                    toast(msgRes = R.string.stake_failed)
                    uiScope { binding.sendButton.changeState(ButtonState.DEFAULT) }
                }
            }
        }
    }

    private suspend fun stake(): Boolean {
        try {
            val txid = CADENCE_STAKE_FLOW.transactionByMainWallet {
                arg { string(data.provider.id) }
                arg { uint32(StakingManager.stakingInfo().delegatorId ?: 0) }
                arg { ufix64Safe(data.amount) }
            }
            val transactionState = TransactionState(
                transactionId = txid!!,
                time = System.currentTimeMillis(),
                state = FlowTransactionStatus.PENDING.num,
                type = TransactionState.TYPE_TRANSACTION_DEFAULT,
                data = ""
            )
            TransactionStateManager.newTransaction(transactionState)
            pushBubbleStack(transactionState)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun checkStakingEnabled(): Boolean {
        return try {
            val response = CADENCE_CHECK_STAKING_ENABLED.executeCadence { }
            response?.parseBool(false) ?: false
        } catch (e: Exception) {
            false
        }
    }


    companion object {
        private const val DATA = "data"
        fun show(activity: FragmentActivity, data: StakingAmountConfirmModel) {
            StakingAmountConfirmDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(DATA, data)
                }
            }.show(activity.supportFragmentManager, "")
        }
    }
}