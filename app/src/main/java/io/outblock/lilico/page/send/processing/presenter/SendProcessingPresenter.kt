package io.outblock.lilico.page.send.processing.presenter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_COIN
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_NFT
import io.outblock.lilico.page.send.processing.SendProcessingDialog
import io.outblock.lilico.page.send.processing.model.SendProcessingDialogModel
import io.outblock.lilico.page.send.transaction.subpage.bindNft
import io.outblock.lilico.page.send.transaction.subpage.bindUserInfo
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope

class SendProcessingPresenter(
    private val fragment: SendProcessingDialog,
    private val binding: DialogSendConfirmBinding,
    private val transactionState: TransactionState,
) : BasePresenter<SendProcessingDialogModel> {

    private val contact by lazy { transactionState.contact() }

    init {
        binding.amountWrapper.setVisible(transactionState.type == TYPE_COIN)
        binding.nftWrapper.setVisible(transactionState.type == TYPE_NFT)
        binding.sendButton.setVisible(false)
        binding.titleView.setText(if (transactionState.type == TYPE_COIN) R.string.coin_send_processing else R.string.nft_send_processing)
        updateState(transactionState)
    }

    override fun bind(model: SendProcessingDialogModel) {
        model.userInfo?.let {
            binding.bindUserInfo(it, contact)
            when (transactionState.type) {
                TYPE_COIN -> setupAmount()
                TYPE_NFT -> ioScope {
                    val nft = transactionState.nftData().nft
                    uiScope { binding.bindNft(nft) }
                }
                else -> {}
            }
        }
        model.amountConvert?.let { updateAmountConvert(it) }
        model.stateChange?.let { updateState(it) }
    }

    private fun updateState(state: TransactionState) {
        with(binding) {
            progressText.setVisible()
            var textColor = R.color.salmon_primary
            var bgColor = R.color.salmon5
            var text = R.string.pending
            var lineRes = R.drawable.ic_transaction_line
            when (state.state) {
                FlowTransactionStatus.SEALED.num -> {
                    textColor = R.color.success3
                    bgColor = R.color.success5
                    text = R.string.success
                    lineRes = R.drawable.ic_transaction_line_success
                }
                FlowTransactionStatus.UNKNOWN.num, FlowTransactionStatus.EXPIRED.num -> {
                    textColor = R.color.warning2
                    bgColor = R.color.warning5
                    text = R.string.failed
                    lineRes = R.drawable.ic_transaction_line_failed
                }
                else -> {}
            }

            with(progressText) {
                setText(text)
                backgroundTintList = ColorStateList.valueOf(bgColor.res2color())
                setTextColor(textColor.res2color())
            }
            lineView.setImageResource(lineRes)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAmount() {
        ioScope {
            val amount = transactionState.coinData().amount.formatPrice()
            uiScope { binding.amountView.text = "$amount Flow" }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAmountConvert(amountConvert: Float) {
        binding.amountConvertView.text = "≈ \$ ${amountConvert.formatPrice()}"
    }

    private fun TransactionState.progressText() = when (state) {
        FlowTransactionStatus.SEALED.num -> R.string.success
        FlowTransactionStatus.UNKNOWN.num, FlowTransactionStatus.EXPIRED.num -> R.string.failed
        else -> R.string.pending
    }
}