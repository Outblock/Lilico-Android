package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListCollectionTitleBinding
import io.outblock.lilico.page.nft.nftlist.NFTFragmentViewModel
import io.outblock.lilico.page.nft.nftlist.model.CollectionTitleModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity

class CollectionTitlePresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<CollectionTitleModel> {
    private val binding by lazy { ItemNftListCollectionTitleBinding.bind(view) }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModel::class.java] }

    init {
        binding.toggleView.setOnClickListener { viewModel.toggleCollectionExpand() }
    }

    override fun bind(model: CollectionTitleModel) {
        view.setVisible()
        binding.textView.text = view.context.getString(R.string.collections_count, model.count)
    }
}