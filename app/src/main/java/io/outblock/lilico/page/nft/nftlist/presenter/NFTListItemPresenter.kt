package io.outblock.lilico.page.nft.nftlist.presenter

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListBinding
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.page.collection.CollectionActivity
import io.outblock.lilico.page.nft.nftdetail.NftDetailActivity
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.page.nft.nftlist.widget.NftItemPopupMenu
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2pix
import io.outblock.lilico.utils.findActivity

class NFTListItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NFTItemModel> {
    private val binding by lazy { ItemNftListBinding.bind(view) }
    private val activity by lazy { findActivity(view) as FragmentActivity }
    private val context = view.context

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2pix() }

    private val isCollectionPage by lazy { activity.javaClass == CollectionActivity::class.java }

    @SuppressLint("SetTextI18n")
    override fun bind(model: NFTItemModel) {
        val nft = model.nft
        val config = NftCollectionConfig.get(nft.collectionAddress)
        with(binding) {
            Glide.with(coverView).load(nft.cover()).transform(RoundedCorners(10.dp2px().toInt())).placeholder(R.drawable.placeholder).into(coverView)
            nameView.text = config?.name ?: nft.contractName()
            priceView.text = "#${nft.id}"

            coverViewWrapper.setOnClickListener {
                NftDetailActivity.launch(context, nft.uniqueId())
            }
            coverViewWrapper.setOnLongClickListener {
                NftItemPopupMenu(coverView, model.nft).show()
                true
            }

            view.setBackgroundResource(R.color.transparent)
            view.setPadding(0, 0, 0, 0)
        }
        view.setOnClickListener {
            NftDetailActivity.launch(context, nft.uniqueId())
        }
    }

    private fun showPopupMenu(model: NFTItemModel) {

    }
}