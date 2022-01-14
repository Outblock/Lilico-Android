package io.outblock.lilico.page.nft.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListBinding
import io.outblock.lilico.page.nft.model.NFTItemModel
import io.outblock.lilico.page.nftdetail.NftDetailActivity
import io.outblock.lilico.utils.toCoverUrl

class NFTListItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NFTItemModel> {
    private val binding by lazy { ItemNftListBinding.bind(view) }
    private val context = view.context

    override fun bind(model: NFTItemModel) {
        val nft = model.nft
        with(binding) {
            Glide.with(coverView).load(nft.media?.uri?.toCoverUrl()).into(coverView)
            nameView.text = nft.title ?: nft.description
            priceView.text = nft.contract.name
            coverViewWrapper.setOnClickListener { NftDetailActivity.launch(context, model.walletAddress, nft.contract.address) }
        }
        view.setOnClickListener { NftDetailActivity.launch(context, model.walletAddress, nft.contract.address) }
    }
}