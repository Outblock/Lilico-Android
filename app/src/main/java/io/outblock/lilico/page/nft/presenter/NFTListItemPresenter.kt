package io.outblock.lilico.page.nft.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemNftListBinding
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.cover
import io.outblock.lilico.page.nft.desc
import io.outblock.lilico.page.nft.model.NFTItemModel
import io.outblock.lilico.page.nft.name
import io.outblock.lilico.page.nftdetail.NftDetailActivity
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.findActivity

class NFTListItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NFTItemModel> {
    private val binding by lazy { ItemNftListBinding.bind(view) }
    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[NFTFragmentViewModel::class.java] }
    private val context = view.context

    override fun bind(model: NFTItemModel) {
        val nft = model.nft
        with(binding) {
            Glide.with(coverView).load(nft.cover()).transform(RoundedCorners(10.dp2px().toInt())).placeholder(R.drawable.placeholder).into(coverView)
            nameView.text = nft.name()
            priceView.text = nft.desc()

            coverViewWrapper.setOnClickListener {
                NftDetailActivity.launch(
                    context,
                    viewModel.getWalletAddress().orEmpty(),
                    nft.contract.address,
                    nft.id.tokenId,
                )
            }
        }
        view.setOnClickListener { NftDetailActivity.launch(context, viewModel.getWalletAddress().orEmpty(), nft.contract.address, nft.id.tokenId) }
    }
}