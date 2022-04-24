package io.outblock.lilico.page.wallet.presenter

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletHeaderBinding
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.page.browser.WebViewActivity
import io.outblock.lilico.page.receive.ReceiveActivity
import io.outblock.lilico.page.send.transaction.TransactionSendActivity
import io.outblock.lilico.page.token.addtoken.AddTokenActivity
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.textToClipboard
import io.outblock.lilico.wallet.toAddress

class WalletHeaderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletHeaderModel> {

    private val binding by lazy { LayoutWalletHeaderBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: WalletHeaderModel) {
        with(binding) {
            walletName.text = "Blowfish Wallet"
            address.text = model.walletList.primaryWalletAddress()?.toAddress()
            balanceNum.text = "$${model.balance.formatPrice()}"

            val count = FlowCoinListManager.coinList().filter { TokenStateManager.isTokenAdded(it.address()) }.count()
            coinCountView.text = view.context.getString(R.string.coins_count, count)

            sendButton.setOnClickListener { TransactionSendActivity.launch(view.context) }
            receiveButton.setOnClickListener { ReceiveActivity.launch(view.context) }
            copyButton.setOnClickListener { copyAddress(address.text.toString()) }
            addButton.setOnClickListener { AddTokenActivity.launch(view.context) }
            buyButton.setOnClickListener { WebViewActivity.open(view.context, "https://www.google.com/search?q=flow") }
        }
    }

    private fun copyAddress(text: String) {
        textToClipboard(text)
        Toast.makeText(view.context, R.string.copy_address_toast.res2String(), Toast.LENGTH_SHORT).show()
    }

}
