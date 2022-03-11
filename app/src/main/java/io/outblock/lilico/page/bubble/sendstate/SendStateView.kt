package io.outblock.lilico.page.bubble.sendstate

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.databinding.WindowSendStateBubbleContentBinding
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay

class SendStateView : MaterialCardView, OnTransactionStateChange {
    private val binding = WindowSendStateBubbleContentBinding.inflate(LayoutInflater.from(context))

    private var transactionState: TransactionState? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        addView(binding.root)
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    override fun onTransactionStateChange() {
        transactionState = TransactionStateManager.getLastVisibleTransaction()
        if (transactionState == null) {
            SendStateBubble.dismiss()
            return
        }

        transactionState?.let { update(it) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onTransactionStateChange()
    }

    fun onDragEnd(gravity: Int, immediately: Boolean = false) {
        ObjectAnimator.ofFloat(this, "translationX", if (gravity == Gravity.START) (-20).dp2px() else 20.dp2px()).apply {
            duration = if (immediately) 10 else 130
            start()
        }

        setBackgroundResource(if (gravity == Gravity.START) R.drawable.bg_round_right_12dp else R.drawable.bg_round_left_12dp)
    }

    private fun update(state: TransactionState) {
        with(binding) {
            progressBar.setProgressWithAnimation(state.progress(), duration = 200)
        }

        if (state.isUnknown() || state.isSealed()) {
            uiScope {
                delay(3000)
                SendStateBubble.dismiss()
            }
        }
    }

    private fun TransactionState.progress(): Float {
        return when (state) {
            FlowTransactionStatus.PENDING.num -> 0.25f
            FlowTransactionStatus.FINALIZED.num -> 0.50f
            FlowTransactionStatus.EXECUTED.num -> 0.75f
            FlowTransactionStatus.SEALED.num -> 1.0f
            else -> 0.0f
        }
    }

    private fun TransactionState.isUnknown() = state == FlowTransactionStatus.UNKNOWN.num || state == FlowTransactionStatus.EXPIRED.num

    private fun TransactionState.isSealed() = state == FlowTransactionStatus.SEALED.num
}