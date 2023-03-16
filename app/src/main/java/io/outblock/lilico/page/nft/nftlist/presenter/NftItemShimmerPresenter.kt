package io.outblock.lilico.page.nft.nftlist.presenter

import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.nft.nftlist.model.NftItemShimmerModel

class NftItemShimmerPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<NftItemShimmerModel> {

    override fun bind(model: NftItemShimmerModel) {
        (view as? ShimmerFrameLayout)?.startShimmer()
    }
}