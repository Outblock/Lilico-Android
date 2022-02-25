package io.outblock.lilico.page.wallet

import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel


val walletListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is WalletHeaderModel && newItem is WalletHeaderModel) {
            return true
        }

        if (oldItem is WalletCoinItemModel && newItem is WalletCoinItemModel) {
            return oldItem.address == newItem.address
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is WalletHeaderModel && newItem is WalletHeaderModel) {
            return oldItem == newItem
        }

        if (oldItem is WalletCoinItemModel && newItem is WalletCoinItemModel) {
            return oldItem == newItem
        }

        return false
    }
}