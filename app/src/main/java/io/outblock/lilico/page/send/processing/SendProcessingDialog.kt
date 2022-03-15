package io.outblock.lilico.page.send.processing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.DialogSendConfirmBinding
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.send.processing.model.SendProcessingDialogModel
import io.outblock.lilico.page.send.processing.presenter.SendProcessingPresenter
import io.outblock.lilico.utils.uiScope

class SendProcessingDialog : BottomSheetDialogFragment() {

    private val state by lazy { arguments?.getParcelable<TransactionState>(EXTRA_STATE)!! }

    private lateinit var binding: DialogSendConfirmBinding

    private lateinit var presenter: SendProcessingPresenter
    private lateinit var viewModel: SendProcessingViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSendConfirmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = SendProcessingPresenter(this, binding, state)
        viewModel = ViewModelProvider(this)[SendProcessingViewModel::class.java].apply {
            bindTransactionState(this@SendProcessingDialog.state)
            userInfoLiveData.observe(this@SendProcessingDialog) { presenter.bind(SendProcessingDialogModel(userInfo = it)) }
            amountConvertLiveData.observe(this@SendProcessingDialog) { presenter.bind(SendProcessingDialogModel(amountConvert = it)) }
            stateChangeLiveData.observe(this@SendProcessingDialog) { presenter.bind(SendProcessingDialogModel(stateChange = it)) }
            load()
        }

        binding.closeButton.setOnClickListener { dismiss() }
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"

        private fun newInstance(state: TransactionState): SendProcessingDialog {
            return SendProcessingDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_STATE, state)
                }
            }
        }

        fun show() {
            uiScope {
                val activity = BaseActivity.getCurrentActivity() ?: return@uiScope
                val state = TransactionStateManager.getLastVisibleTransaction() ?: return@uiScope
                newInstance(state).show(activity.supportFragmentManager, "")
            }
        }
    }
}