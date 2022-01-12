package io.outblock.lilico.page.nft.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListBinding
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.toCoverUrl

class NFTListItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Nft> {
    private val binding by lazy { ItemNftListBinding.bind(view) }
    private val context = view.context

    override fun bind(model: Nft) {
        with(binding) {
            Glide.with(coverView).load(model.media.uri.toCoverUrl()).into(coverView)
            nameView.text = model.title
            priceView.text = "123 Flow"
        }
        view.setOnClickListener {  }
    }
}