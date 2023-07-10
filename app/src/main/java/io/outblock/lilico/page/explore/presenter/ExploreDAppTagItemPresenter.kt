package io.outblock.lilico.page.explore.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemDappCategoryBinding
import io.outblock.lilico.page.explore.ExploreViewModel
import io.outblock.lilico.page.explore.model.DAppTagModel
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.findActivity

class ExploreDAppTagItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<DAppTagModel> {
    private val binding by lazy { ItemDappCategoryBinding.bind(view) }

    private val activity = findActivity(view)

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[ExploreViewModel::class.java] }

    override fun bind(model: DAppTagModel) {
        with(binding) {
            textView.text = model.category
            textView.setTextColor(if (model.isSelected) R.color.text.res2color() else R.color.text_sub.res2color())

            root.strokeColor = if (model.isSelected) R.color.violet1.res2color() else R.color.bg_icon.res2color()
            view.setOnClickListener {
                viewModel.selectDappTag(model.category)
            }
        }
    }
}