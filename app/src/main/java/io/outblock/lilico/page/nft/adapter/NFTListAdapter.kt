package io.outblock.lilico.page.nft.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.page.nft.model.HeaderPlaceholderModel
import io.outblock.lilico.page.nft.model.NFTItemModel
import io.outblock.lilico.page.nft.model.NFTTitleModel
import io.outblock.lilico.page.nft.nftListDiffCallback
import io.outblock.lilico.page.nft.presenter.HeaderPlaceholderPresenter
import io.outblock.lilico.page.nft.presenter.NFTListItemPresenter
import io.outblock.lilico.page.nft.presenter.SelectionItemPresenter
import io.outblock.lilico.page.nft.presenter.TitleItemPresenter

class NFTListAdapter : BaseAdapter<Any>(nftListDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NFTItemModel -> TYPE_NFT
            is NFTTitleModel -> TYPE_TITLE
            is HeaderPlaceholderModel -> TYPE_GRID_HEADER
            is NftSelections -> TYPE_SELECTION
            else -> TYPE_NFT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NFT -> NFTListItemPresenter(parent.inflate(R.layout.item_nft_list))
            TYPE_TITLE -> TitleItemPresenter(parent.inflate(R.layout.item_nft_list_title))
            TYPE_GRID_HEADER -> HeaderPlaceholderPresenter(parent.inflate(R.layout.item_nft_list_grid_header_placeholder))
            TYPE_SELECTION -> SelectionItemPresenter(parent.inflate(R.layout.item_nft_list_selections))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NFTListItemPresenter -> holder.bind(getItem(position) as NFTItemModel)
            is TitleItemPresenter -> holder.bind(getItem(position) as NFTTitleModel)
            is SelectionItemPresenter -> holder.bind(getItem(position) as NftSelections)
        }
    }

    companion object {
        private const val TYPE_NFT = 1
        private const val TYPE_TITLE = 2
        private const val TYPE_GRID_HEADER = 3
        private const val TYPE_SELECTION = 4
    }
}