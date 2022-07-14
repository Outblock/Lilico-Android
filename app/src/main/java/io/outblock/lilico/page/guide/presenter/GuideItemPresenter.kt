package io.outblock.lilico.page.guide.presenter

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemGuidePageBinding
import io.outblock.lilico.page.guide.model.GuideItemModel

class GuideItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<GuideItemModel> {

    private val binding by lazy { ItemGuidePageBinding.bind(view) }

    override fun bind(model: GuideItemModel) {
        with(binding) {
            coverView.setImageResource(model.cover)
            titleView.setText(model.title)
            descView.setText(model.desc)
        }
    }
}