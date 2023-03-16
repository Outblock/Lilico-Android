package io.outblock.lilico.page.wallet.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.page.wallet.presenter.WalletCoinItemPresenter
import io.outblock.lilico.page.wallet.presenter.WalletHeaderPresenter
import io.outblock.lilico.page.wallet.walletListDiffCallback

class WalletFragmentAdapter : BaseAdapter<Any>(walletListDiffCallback) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WalletHeaderModel -> TYPE_WALLET_HEADER
            is WalletCoinItemModel -> TYPE_WALLET_COIN_ITEM
            else -> TYPE_WALLET_COIN_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WALLET_HEADER -> WalletHeaderPresenter(parent.inflate(R.layout.layout_wallet_header))
            TYPE_WALLET_COIN_ITEM -> WalletCoinItemPresenter(parent.inflate(R.layout.layout_wallet_coin_item))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WalletHeaderPresenter -> holder.bind(getItem(position) as WalletHeaderModel)
            is WalletCoinItemPresenter -> holder.bind(getItem(position) as WalletCoinItemModel)
        }
    }

    companion object {
        private const val TYPE_WALLET_HEADER = 1
        private const val TYPE_WALLET_COIN_ITEM = 2
    }
}