package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.CollectionData
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter.AccessibleCoinPresenter
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter.AccessibleNftCollectionPresenter
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel

class AccessibleListAdapter : BaseAdapter<Any>(diffUtils) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CollectionData -> TYPE_NFT
            is WalletCoinItemModel -> TYPE_TOKEN
            else -> TYPE_TOKEN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NFT -> AccessibleNftCollectionPresenter(parent.inflate(R.layout.item_accessible_nft))
            TYPE_TOKEN -> AccessibleCoinPresenter(parent.inflate(R.layout.item_accessible_coin))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AccessibleNftCollectionPresenter -> holder.bind(getItem(position) as CollectionData)
            is AccessibleCoinPresenter -> holder.bind(getItem(position) as WalletCoinItemModel)
        }
    }

    companion object {
        private const val TYPE_NFT = 1
        private const val TYPE_TOKEN = 2
    }
}

private val diffUtils = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is CollectionData && newItem is CollectionData) {
            return oldItem.id == newItem.id
        }
        if (oldItem is WalletCoinItemModel && newItem is WalletCoinItemModel) {
            return oldItem.coin.name == newItem.coin.name
        }
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is CollectionData && newItem is CollectionData) {
            return oldItem == newItem
        }
        if (oldItem is WalletCoinItemModel && newItem is WalletCoinItemModel) {
            return oldItem.coin == newItem.coin
        }
        return oldItem == newItem
    }
}