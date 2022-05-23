package io.outblock.lilico.page.explore.presenter

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemExploreBookmarkTitleBinding
import io.outblock.lilico.page.explore.model.BookmarkTitleModel
import io.outblock.lilico.utils.findActivity

class ExploreBookmarkTitleItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<BookmarkTitleModel> {

    private val binding by lazy { ItemExploreBookmarkTitleBinding.bind(view) }

    private val activity = findActivity(view)

    override fun bind(model: BookmarkTitleModel) {
        with(binding) {
            titleView.text = model.title
            iconView.setImageResource(model.icon)
        }
    }
}