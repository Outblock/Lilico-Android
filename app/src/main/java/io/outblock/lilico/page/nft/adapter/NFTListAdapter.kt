package io.outblock.lilico.page.nft.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.presenter.NFTListItemPresenter

class NFTListAdapter : BaseAdapter<Any>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NFTListItemPresenter(parent.inflate(R.layout.item_nft_list))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NFTListItemPresenter).bind(getItem(position) as Nft)
    }
}