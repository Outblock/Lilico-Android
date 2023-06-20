package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.databinding.DialogUnlinkChildAccountBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.flowjvm.CADENCE_UNLINK_CHILD_ACCOUNT
import io.outblock.lilico.manager.flowjvm.transactionByMainWallet
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.toast
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.toAddress
import io.outblock.lilico.widgets.ButtonState

class ChildAccountUnlinkDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogUnlinkChildAccountBinding
    private val data by lazy { arguments?.getParcelable<ChildAccount>(EXTRA_DATA) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogUnlinkChildAccountBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val account = data ?: return
        binding.closeButton.setOnClickListener { dismiss() }
        with(binding) {
            dappIcon.loadAvatar(account.icon)
            dappName.text = account.name
            dappAddress.text = account.address

            ioScope {
                val userInfo = userInfoCache().read() ?: return@ioScope
                val address = WalletManager.wallet()?.walletAddress() ?: return@ioScope

                uiScope {
                    walletIcon.loadAvatar(userInfo.avatar)
                    walletName.setText(R.string.wallet)
                    walletAddress.text = address
                }
            }
        }

        binding.startButton.setOnProcessing {
            sendUnlinkTransaction(account)
        }
    }

    private fun sendUnlinkTransaction(account: ChildAccount) {
        ioScope {
            val transactionId = CADENCE_UNLINK_CHILD_ACCOUNT.transactionByMainWallet {
                arg { address(account.address.toAddress()) }
            }

            if (transactionId.isNullOrBlank()) {
                uiScope { binding.startButton.changeState(ButtonState.DEFAULT) }
                toast(R.string.unlink_fail)
                return@ioScope
            }
            val transactionState = TransactionState(
                transactionId = transactionId,
                time = System.currentTimeMillis(),
                state = FlowTransactionStatus.PENDING.num,
                type = TransactionState.TYPE_TRANSACTION_DEFAULT,
                data = Gson().toJson(account),
            )
            TransactionStateManager.newTransaction(transactionState)
            pushBubbleStack(transactionState)
            uiScope { activity?.finish() }
        }
    }

    companion object {
        private const val EXTRA_DATA = "extra_data"
        fun show(fragmentManager: FragmentManager, account: ChildAccount) {
            ChildAccountUnlinkDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, account)
                }
            }.show(fragmentManager, "")
        }
    }
}