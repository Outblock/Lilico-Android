package io.outblock.lilico.page.nft

import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel

val nftListDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem.contract.address == newItem.contract.address
        }

        return true
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Nft && newItem is Nft) {
            return oldItem == newItem
        }

        return true
    }
}