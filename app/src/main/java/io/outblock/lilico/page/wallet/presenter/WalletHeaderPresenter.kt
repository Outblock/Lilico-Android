package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletHeaderBinding
import io.outblock.lilico.page.send.TransactionSendActivity
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.textToClipboard
import io.outblock.lilico.wallet.toAddress

class WalletHeaderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletHeaderModel> {

    private val binding by lazy { LayoutWalletHeaderBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletHeaderModel) {
        val wallet = model.walletList.primaryWallet() ?: return
        with(binding) {
            walletName.text = "Blowfish Wallet"
            address.text = wallet.blockchain.first().address.toAddress()
            balanceNum.text = "$${model.balance}"

            sendButton.setOnClickListener { TransactionSendActivity.launch(view.context) }
            copyButton.setOnClickListener { copyAddress(address.text.toString()) }
        }
    }

    private fun copyAddress(text: String) {
        textToClipboard(text)
        Toast.makeText(view.context, R.string.copy_address_toast.res2String(), Toast.LENGTH_SHORT).show()
    }

}