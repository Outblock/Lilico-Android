package io.outblock.lilico.page.wallet.presenter

import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.utils.extensions.setVisible

class WalletHeaderPlaceholderPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Boolean> {

    override fun bind(model: Boolean) {
        view.setVisible(model)
        (view as ShimmerFrameLayout).startShimmer()
    }
}