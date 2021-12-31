package io.outblock.lilico.page.wallet.presenter

import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder

class WalletHeaderPlaceholderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Any> {


    override fun bind(model: Any) {
        view.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container).startShimmer()
    }

}