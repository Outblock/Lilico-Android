package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListTitleBinding
import io.outblock.lilico.page.nft.nftlist.model.NFTTitleModel
import io.outblock.lilico.utils.extensions.res2pix

class TitleItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NFTTitleModel> {
    private val binding by lazy { ItemNftListTitleBinding.bind(view) }

    override fun bind(model: NFTTitleModel) {
        with(binding) {
            iconView.setImageResource(model.icon)
            iconView.setColorFilter(model.iconTint)

            textView.text = model.text
            textView.setTextColor(model.textColor)
        }
        with(view) {
            setPadding(R.dimen.nft_list_divider_size.res2pix(), paddingTop, paddingRight, paddingBottom)
        }
    }
}