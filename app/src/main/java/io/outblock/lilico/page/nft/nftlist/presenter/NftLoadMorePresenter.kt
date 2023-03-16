package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.collection.CollectionActivity
import io.outblock.lilico.page.collection.CollectionViewModel
import io.outblock.lilico.page.nft.nftlist.NftViewModel
import io.outblock.lilico.page.nft.nftlist.model.NftLoadMoreModel
import io.outblock.lilico.utils.findActivity

class NftLoadMorePresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NftLoadMoreModel> {

    override fun bind(model: NftLoadMoreModel) {
        if (model.isGridLoadMore == true) requestGridNextPage()
        if (model.isListLoadMore == true) requestListNextPage()
    }

    private fun viewModel(): ViewModel? {
        val activity = findActivity(view) ?: return null
        return if (activity.javaClass == CollectionActivity::class.java) {
            ViewModelProvider(activity as FragmentActivity)[CollectionViewModel::class.java]
        } else {
            ViewModelProvider(activity as FragmentActivity)[NftViewModel::class.java]
        }
    }

    private fun requestListNextPage() {
        (viewModel() as? CollectionViewModel)?.requestListNextPage()
        (viewModel() as? NftViewModel)?.requestListNextPage()
    }

    private fun requestGridNextPage() {
        (viewModel() as? NftViewModel)?.requestGridNextPage()
    }
}