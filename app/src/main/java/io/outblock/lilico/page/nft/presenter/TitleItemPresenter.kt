package io.outblock.lilico.page.nft.presenter

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListTitleBinding
import io.outblock.lilico.page.nft.model.NFTTitleModel

class TitleItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NFTTitleModel> {
    private val binding by lazy { ItemNftListTitleBinding.bind(view) }

    override fun bind(model: NFTTitleModel) {
        with(binding) {
            iconView.setImageResource(model.icon)
            iconView.setColorFilter(model.iconTint)
            textView.text = model.text
        }
    }
}