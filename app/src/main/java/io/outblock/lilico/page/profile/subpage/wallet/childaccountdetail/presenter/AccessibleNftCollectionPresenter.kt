package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAccessibleNftBinding
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.CollectionData
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.loadAvatar

class AccessibleNftCollectionPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<CollectionData> {

    private val binding by lazy { ItemAccessibleNftBinding.bind(view) }

    override fun bind(model: CollectionData) {
        with(binding) {
            view.setOnClickListener {
                // todo jump to NFTCollectionDetail model.path
                // '/storage/MomentCollection'.split('/').last
            }
            iconView.loadAvatar(model.logo)
            titleView.text = model.name
            collectionCountView.text = view.context.getString(R.string.collections_count, model.idList.size)
            arrowView.setVisible(model.idList.isNotEmpty())
        }
    }
}