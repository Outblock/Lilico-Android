package io.outblock.lilico.page.dialog.processing.fcl

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.DialogFclAuthzBinding
import io.outblock.lilico.manager.config.isGasFree
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.uiScope


class FclTransactionProcessingDialog : BottomSheetDialogFragment(), OnTransactionStateChange {

    private val txId by lazy { arguments?.getString(EXTRA_TX_ID)!! }

    private lateinit var binding: DialogFclAuthzBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFclAuthzBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        TransactionStateManager.addOnTransactionStateChange(this)
        with(binding) {
            actionButton.setVisible(false)
            uiScope { feeNumber.text = if (isGasFree()) "0" else "0.001" }
            scriptHeaderWrapper.setOnClickListener { toggleScriptVisible() }
            updateState()
        }
    }

    override fun onTransactionStateChange() {
        updateState()
    }

    private fun updateState() {
        val state = TransactionStateManager.getTransactionStateById(txId) ?: return
        if (state.type != TransactionState.TYPE_FCL_TRANSACTION) {
            return
        }
        val data = state.fclTransactionData()
        with(binding) {
            Glide.with(iconView).load(data.url?.toFavIcon()).placeholder(R.drawable.placeholder).into(iconView)
            nameView.text = data.title
            scriptTextView.text = data.voucher.cadence?.trimIndent()
            updateProcessing(state)
        }
    }

    private fun updateProcessing(state: TransactionState) {
        with(binding.progressText) {
            setVisible()
            var textColor = R.color.salmon_primary
            var bgColor = R.color.salmon5
            var text = R.string.pending
            when (state.state) {
                FlowTransactionStatus.SEALED.num -> {
                    textColor = R.color.success3
                    bgColor = R.color.success5
                    text = R.string.success
                }
                FlowTransactionStatus.UNKNOWN.num, FlowTransactionStatus.EXPIRED.num -> {
                    textColor = R.color.warning2
                    bgColor = R.color.warning5
                    text = R.string.failed
                }
                else -> {}
            }

            setText(text)
            backgroundTintList = ColorStateList.valueOf(bgColor.res2color())
            setTextColor(textColor.res2color())
        }
    }

    private fun toggleScriptVisible() {
        with(binding) {
            TransitionManager.go(Scene(scriptLayout), TransitionSet().apply {
                addTransition(ChangeBounds().apply { duration = 150 })
                addTransition(Fade(Fade.IN).apply { duration = 150 })
            })
            val toVisible = !scriptTextWrapper.isVisible()
            scriptTextWrapper.setVisible(toVisible)
            scriptArrow.rotation = if (toVisible) 0f else 270f
        }
    }

    companion object {
        private const val EXTRA_TX_ID = "extra_tx_id"

        fun show(txId: String) {
            val activity = BaseActivity.getCurrentActivity() ?: return
            FclTransactionProcessingDialog().apply {
                arguments = Bundle().apply { putString(EXTRA_TX_ID, txId) }
            }.show(activity.supportFragmentManager, "")
        }
    }
}