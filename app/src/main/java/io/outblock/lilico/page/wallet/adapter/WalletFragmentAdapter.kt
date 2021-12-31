package io.outblock.lilico.page.wallet.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.page.wallet.model.WalletHeaderPlaceholderModel
import io.outblock.lilico.page.wallet.presenter.WalletHeaderPlaceholderPresenter
import io.outblock.lilico.page.wallet.presenter.WalletHeaderPresenter

class WalletFragmentAdapter : BaseAdapter<Any>() {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WalletHeaderModel -> TYPE_WALLET_HEADER
            is WalletHeaderPlaceholderModel -> TYPE_WALLET_HEADER_PLACEHOLDER
            else -> TYPE_WALLET_COIN_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WALLET_HEADER_PLACEHOLDER -> WalletHeaderPlaceholderPresenter(parent.inflate(R.layout.layout_wallet_header_placeholder))
            TYPE_WALLET_HEADER -> WalletHeaderPresenter(parent.inflate(R.layout.layout_wallet_header))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WalletHeaderPresenter -> holder.bind(getItem(position) as WalletHeaderModel)
            is WalletHeaderPlaceholderPresenter -> holder.bind(position)
        }
    }

    companion object {
        private const val TYPE_WALLET_HEADER_PLACEHOLDER = 0
        private const val TYPE_WALLET_HEADER = 1
        private const val TYPE_WALLET_COIN_ITEM = 2
    }
}