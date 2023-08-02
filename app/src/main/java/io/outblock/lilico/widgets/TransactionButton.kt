package io.outblock.lilico.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import io.outblock.lilico.R
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.utils.extensions.setVisible

class TransactionButton : FrameLayout, OnTransactionStateChange {

    private var defaultText: String
    private var processingText: String

    private val pendingWrapper by lazy { findViewById<View>(R.id.pending_wrapper) }

    private val sendButton by lazy { findViewById<SendButton>(R.id.button) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SendButton, defStyleAttr, 0)
        defaultText = array.getString(R.styleable.SendButton_defaultText).orEmpty()
        processingText = array.getString(R.styleable.SendButton_processingText).orEmpty()
        array.recycle()

        init()
    }

    fun init() {
        LayoutInflater.from(context).inflate(R.layout.wdiget_transaction_button, this)

        sendButton.updateDefaultText(defaultText)
        sendButton.updateProcessingText(processingText)

        TransactionStateManager.addOnTransactionStateChange(this)

        checkPendingState()
    }

    fun button(): SendButton = sendButton

    override fun onTransactionStateChange() {
        checkPendingState()
    }

    private fun checkPendingState() {
        val isPending = TransactionStateManager.getProcessingTransaction().isNotEmpty()

        pendingWrapper.setVisible(isPending)
        sendButton.isEnabled = !isPending
    }
}