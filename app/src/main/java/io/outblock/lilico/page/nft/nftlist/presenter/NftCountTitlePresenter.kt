package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftCountTitleBinding
import io.outblock.lilico.page.nft.nftlist.model.NFTCountTitleModel

class NftCountTitlePresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NFTCountTitleModel> {
    private val binding by lazy { ItemNftCountTitleBinding.bind(view) }

    override fun bind(model: NFTCountTitleModel) {
        with(binding) {
            textView.text = view.context.getString(R.string.nft_count, model.count)
        }
    }
}