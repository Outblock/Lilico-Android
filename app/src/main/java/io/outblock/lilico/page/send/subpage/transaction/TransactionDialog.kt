package io.outblock.lilico.page.send.subpage.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogTransactionDialogBinding
import io.outblock.lilico.page.send.subpage.amount.model.TransactionModel
import io.outblock.lilico.page.send.subpage.transaction.model.TransactionDialogModel
import io.outblock.lilico.page.send.subpage.transaction.presenter.TransactionPresenter

class TransactionDialog : BottomSheetDialogFragment() {

    private val transaction by lazy { arguments?.getParcelable<TransactionModel>(EXTRA_TRANSACTION)!! }

    private lateinit var binding: DialogTransactionDialogBinding

    private lateinit var presenter: TransactionPresenter
    private lateinit var viewModel: TransactionViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogTransactionDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = TransactionPresenter(this, binding)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java].apply {
            bindTransaction(this@TransactionDialog.transaction)
            userInfoLiveData.observe(this@TransactionDialog) { presenter.bind(TransactionDialogModel(userInfo = it)) }
            amountConvertLiveData.observe(this@TransactionDialog) { presenter.bind(TransactionDialogModel(amountConvert = it)) }
            resultLiveData.observe(this@TransactionDialog) {
                presenter.bind(TransactionDialogModel(isSendSuccess = it))
                if (it) {
                    dismiss()
                }
            }
            load()
        }

        binding.closeButton.setOnClickListener { dismiss() }
    }

    companion object {
        private const val EXTRA_TRANSACTION = "extra_transaction"

        fun newInstance(transaction: TransactionModel): TransactionDialog {
            return TransactionDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_TRANSACTION, transaction)
                }
            }
        }
    }
}