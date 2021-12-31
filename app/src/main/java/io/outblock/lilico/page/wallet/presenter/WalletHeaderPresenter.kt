package io.outblock.lilico.page.wallet.presenter

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.LayoutWalletHeaderBinding
import io.outblock.lilico.page.wallet.model.WalletHeaderModel

class WalletHeaderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletHeaderModel> {

    private val binding by lazy { LayoutWalletHeaderBinding.bind(view) }

    override fun bind(model: WalletHeaderModel) {
        val wallet = model.walletList.primaryWallet() ?: return
        with(binding) {
            walletName.text = "Blowfish Wallet"
            address.text = wallet.blockchain.first().address
//            balanceNum.text = model.balance
        }
    }

}