package io.outblock.lilico.page.nft.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.model.GridHeaderPlaceholderModel
import io.outblock.lilico.page.nft.model.NFTItemModel
import io.outblock.lilico.page.nft.model.NFTTitleModel
import io.outblock.lilico.page.nft.nftListDiffCallback
import io.outblock.lilico.page.nft.presenter.GridHeaderPlaceholderPresenter
import io.outblock.lilico.page.nft.presenter.NFTListItemPresenter
import io.outblock.lilico.page.nft.presenter.TitleItemPresenter

class NFTListAdapter : BaseAdapter<Any>(nftListDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NFTItemModel -> TYPE_NFT
            is NFTTitleModel -> TYPE_TITLE
            is GridHeaderPlaceholderModel -> TYPE_GRID_HEADER
            else -> TYPE_NFT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NFT -> NFTListItemPresenter(parent.inflate(R.layout.item_nft_list))
            TYPE_TITLE -> TitleItemPresenter(parent.inflate(R.layout.item_nft_list_title))
            TYPE_GRID_HEADER -> GridHeaderPlaceholderPresenter(parent.inflate(R.layout.item_nft_list_grid_header_placeholder))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NFTListItemPresenter -> holder.bind(getItem(position) as NFTItemModel)
            is TitleItemPresenter->holder.bind(getItem(position) as NFTTitleModel)
        }
    }

    companion object {
        private const val TYPE_NFT = 1
        private const val TYPE_TITLE = 2
        private const val TYPE_GRID_HEADER = 3
    }
}