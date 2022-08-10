package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.nft.nftlist.NftViewModel
import io.outblock.lilico.page.nft.nftlist.model.NftLoadMoreModel
import io.outblock.lilico.utils.findActivity

class NftLoadMorePresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NftLoadMoreModel> {
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NftViewModel::class.java] }

    override fun bind(model: NftLoadMoreModel) {
        if (model.isGridLoadMore == true) viewModel.requestGridNextPage()
        if (model.isListLoadMore == true) viewModel.requestListNextPage()
    }
}