package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.transition.TransitionManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletHeaderBinding
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.page.receive.ReceiveActivity
import io.outblock.lilico.page.send.transaction.TransactionSendActivity
import io.outblock.lilico.page.token.addtoken.AddTokenActivity
import io.outblock.lilico.page.transaction.record.TransactionRecordActivity
import io.outblock.lilico.page.wallet.WalletFragmentViewModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.wallet.toAddress

class WalletHeaderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletHeaderModel?> {

    private val binding by lazy { LayoutWalletHeaderBinding.bind(view) }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[WalletFragmentViewModel::class.java] }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletHeaderModel?) {
        binding.root.setVisible(model != null)
        model ?: return

        with(binding) {
            walletName.text = "Wallet"
            uiScope {
                val isHideBalance = isHideWalletBalance()
                address.diffSetText(if (isHideBalance) "******************" else model.walletList.primaryWalletAddress()?.toAddress())
                balanceNum.diffSetText(if (isHideBalance) "****" else "$${model.balance.formatPrice()}")
                hideButton.setImageResource(if (isHideBalance) R.drawable.ic_eye_off else R.drawable.ic_eye_on)
            }

            val count = FlowCoinListManager.coinList().filter { TokenStateManager.isTokenAdded(it.address()) }.count()
            coinCountView.text = view.context.getString(R.string.coins_count, count)

            sendButton.setOnClickListener { TransactionSendActivity.launch(view.context) }
            receiveButton.setOnClickListener { ReceiveActivity.launch(view.context) }
            copyButton.setOnClickListener { copyAddress(address.text.toString()) }
            addButton.setOnClickListener { AddTokenActivity.launch(view.context) }
            buyButton.setOnClickListener { toast(msgRes = R.string.future_coming_soon) }

            hideButton.setOnClickListener {
                uiScope {
                    setHideWalletBalance(!isHideWalletBalance())
                    bind(model)
                    viewModel.onBalanceHideStateUpdate()
                }
            }

            bindTransactionCount(model.transactionCount)
            bindDomain(model.walletList.username)
        }
    }

    private fun bindTransactionCount(transactionCount: Int?) {
        val count = transactionCount ?: 0
        TransitionManager.beginDelayedTransition(binding.root)
        binding.transactionCountWrapper.setVisible(count > 0)
        binding.transactionCountView.text = "$transactionCount"
        binding.transactionCountWrapper.setOnClickListener { TransactionRecordActivity.launch(view.context) }
    }

    private fun copyAddress(text: String) {
        textToClipboard(text)
        Toast.makeText(view.context, R.string.copy_address_toast.res2String(), Toast.LENGTH_SHORT).show()
    }

    private fun TextView.diffSetText(text: String?) {
        if (text != this.text.toString()) {
            this.text = text
        }
    }

    private fun bindDomain(username: String) {
        uiScope {
            binding.domainWrapper.setVisible(isMeowDomainClaimed())
            binding.domainView.text = "$username.meow"
        }
    }
}
