package io.outblock.lilico.page.wallet

import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.logd


val walletListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is WalletHeaderModel && newItem is WalletHeaderModel) {
            return true
        }

        if (oldItem is WalletCoinItemModel && newItem is WalletCoinItemModel) {
            return oldItem.coin.symbol == newItem.coin.symbol
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is WalletHeaderModel && newItem is WalletHeaderModel) {
            return oldItem == newItem
        }

        if (oldItem is WalletCoinItemModel && newItem is WalletCoinItemModel) {
            logd("xxxxx", "oldItem == newItem:${oldItem == newItem}")
            return oldItem == newItem
        }

        return false
    }
}