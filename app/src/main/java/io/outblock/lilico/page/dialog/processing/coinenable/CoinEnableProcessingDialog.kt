package io.outblock.lilico.page.dialog.processing.coinenable

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.DialogAddTokenProcessingBinding
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.uiScope

class CoinEnableProcessingDialog : BottomSheetDialogFragment() {

    private val state by lazy { arguments?.getParcelable<TransactionState>(EXTRA_STATE)!! }

    private lateinit var binding: DialogAddTokenProcessingBinding

    private lateinit var viewModel: CoinEnableProcessingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAddTokenProcessingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.closeButton.setOnClickListener { dismiss() }
        with(binding) {
            val coin = state.tokenData()
            tokenNameView.text = coin.name
            Glide.with(iconView).load(coin.icon).into(iconView)
            updateState(state)
        }

        viewModel = ViewModelProvider(this)[CoinEnableProcessingViewModel::class.java].apply {
            bindTransactionState(this@CoinEnableProcessingDialog.state)
            stateChangeLiveData.observe(this@CoinEnableProcessingDialog) { updateState(it) }
        }
    }

    private fun updateState(transactionState: TransactionState) {
        with(binding.processingStateView) {
            text = transactionState.stateStr()
            if (transactionState.isSuccess()) {
                backgroundTintList = ColorStateList.valueOf(R.color.success5.res2color())
                setTextColor(R.color.success3.res2color())
            }
        }
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"

        private fun newInstance(state: TransactionState): CoinEnableProcessingDialog {
            return CoinEnableProcessingDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_STATE, state)
                }
            }
        }

        fun show(state: TransactionState) {
            uiScope {
                val activity = BaseActivity.getCurrentActivity() ?: return@uiScope
                newInstance(state).show(activity.supportFragmentManager, "")
            }
        }
    }
}