package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListCollectionLineBinding
import io.outblock.lilico.page.collection.CollectionActivity
import io.outblock.lilico.page.nft.nftlist.model.CollectionItemModel
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.page.profile.subpage.wallet.ChildAccountCollectionManager
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.setVisible

class CollectionLineItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<CollectionItemModel> {
    private val binding by lazy { ItemNftListCollectionLineBinding.bind(view) }

    private val corner by lazy { 12.dp2px().toInt() }

    override fun bind(model: CollectionItemModel) {
        with(binding) {
            val config = model.collection
            nameView.text = config.name
            countView.text = view.context.getString(R.string.collectibles_count, model.count)
            Glide.with(coverView).load(config.logo).transform(CenterCrop(), RoundedCorners(corner)).into(coverView)
        }
        bindAccessible(model)
        view.setOnClickListener { CollectionActivity.launch(view.context, model.collection.contractName) }
    }

    private fun bindAccessible(model: CollectionItemModel) {
        val accessible = ChildAccountCollectionManager.isNFTCollectionAccessible(model.collection.id)
        binding.countView.setVisible(accessible)
        binding.tvInaccessibleTag.setVisible(accessible.not())
    }
}