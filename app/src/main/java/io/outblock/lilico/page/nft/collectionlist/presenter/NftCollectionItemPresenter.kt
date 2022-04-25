package io.outblock.lilico.page.nft.collectionlist.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftCollectionListBinding
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.nft.collectionlist.NftEnableConfirmDialog
import io.outblock.lilico.page.nft.collectionlist.model.NftCollectionItem
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity
import jp.wasabeef.glide.transformations.BlurTransformation

class NftCollectionItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NftCollectionItem> {

    private val binding by lazy { ItemNftCollectionListBinding.bind(view) }

    override fun bind(model: NftCollectionItem) {
        with(binding) {
            nameView.text = model.collection.name
            descView.text = model.collection.description
            Glide.with(coverView).load(model.collection.banner).transform(BlurTransformation(2, 5)).into(coverView)
            stateButton.setOnClickListener {
                if (model.isNormalState()) {
                    NftEnableConfirmDialog.show((findActivity(view) as FragmentActivity).supportFragmentManager, model.collection)
                }
            }
            progressBar.setVisible(model.isAdding == true)
            stateButton.setVisible(model.isAdding != true)
            titleWrapper.setOnClickListener { openBrowser(findActivity(view)!!, model.collection.officialWebsite) }
            stateButton.setImageResource(if (model.isNormalState()) R.drawable.ic_baseline_add_24_salmon_primary else R.drawable.ic_check_round)
        }
    }
}