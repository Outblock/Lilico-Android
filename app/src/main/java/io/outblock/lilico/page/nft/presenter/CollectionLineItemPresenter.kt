package io.outblock.lilico.page.nft.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListCollectionLineBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.page.collection.CollectionActivity
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.model.CollectionItemModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.findActivity

class CollectionLineItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<CollectionItemModel> {
    private val binding by lazy { ItemNftListCollectionLineBinding.bind(view) }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModel::class.java] }

    private val corner by lazy { 12.dp2px().toInt() }

    override fun bind(model: CollectionItemModel) {
        with(binding) {
            val config = NftCollectionConfig.get(model.address) ?: return@with
            nameView.text = config.name
            countView.text = view.context.getString(R.string.collections_count, model.count)
            Glide.with(coverView).load(config.logo).transform(CenterCrop(), RoundedCorners(corner)).into(coverView)
        }
        view.setOnClickListener { CollectionActivity.launch(view.context, viewModel.getWalletAddress()!!, model.address) }
    }
}