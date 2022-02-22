package io.outblock.lilico.page.send.subpage.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogTransactionDialogBinding
import io.outblock.lilico.page.send.subpage.amount.model.TransactionModel
import io.outblock.lilico.page.send.subpage.transaction.model.TransactionDialogModel
import io.outblock.lilico.page.send.subpage.transaction.presenter.TransactionPresenter
import io.outblock.lilico.utils.getActivityFromContext

class TransactionDialog(
    private val context: Context,
    val transaction: TransactionModel,
) {
    private var dialog: Dialog? = null

    fun show() {
        with(AlertDialog.Builder(context, R.style.Theme_AlertDialogTheme)) {
            setView(TransactionView(context, this@TransactionDialog))
            setCancelable(false)
            with(create()) {
                dialog = this
                show()
            }
        }
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}

@SuppressLint("ViewConstructor")
private class TransactionView(
    context: Context,
    private val dialog: TransactionDialog,
) : FrameLayout(context) {
    private val binding by lazy { DialogTransactionDialogBinding.bind(findViewById(R.id.rootView)) }

    private val transaction = dialog.transaction
    private val activity by lazy { getActivityFromContext(context) as FragmentActivity }

    private var presenter: TransactionPresenter
    private var viewModel: TransactionViewModel

    init {
        LayoutInflater.from(context).inflate(R.layout.dialog_transaction_dialog, this)

        presenter = TransactionPresenter(activity, binding)
        viewModel = ViewModelProvider(activity)[TransactionViewModel::class.java].apply {
            bindTransaction(this@TransactionView.transaction)
            userInfoLiveData.observe(activity) { presenter.bind(TransactionDialogModel(userInfo = it)) }
            amountConvertLiveData.observe(activity) { presenter.bind(TransactionDialogModel(amountConvert = it)) }
            resultLiveData.observe(activity) {
                presenter.bind(TransactionDialogModel(isSendSuccess = it))
                if (it) {
                    dialog.dismiss()
                }
            }
            load()
        }

        binding.closeButton.setOnClickListener { dialog.dismiss() }
    }

}