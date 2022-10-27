package io.outblock.lilico.page.profile.subpage.walletconnect.session.presenter

import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemWalletConnectSessionBinding
import io.outblock.lilico.manager.walletconnect.dispatch
import io.outblock.lilico.manager.walletconnect.model.toWcRequest
import io.outblock.lilico.page.profile.subpage.walletconnect.session.model.PendingRequestModel
import io.outblock.lilico.utils.extensions.urlHost
import io.outblock.lilico.utils.ioScope

class WalletConnectPendingItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<PendingRequestModel> {

    private val binding by lazy { ItemWalletConnectSessionBinding.bind(view) }

    override fun bind(model: PendingRequestModel) {
        val meta = model.metadata ?: return
        with(binding) {
            Glide.with(coverView).load(meta.icons.firstOrNull()).into(coverView)
            titleView.text = meta.name
            descView.text = meta.url.urlHost()
        }
        view.setOnClickListener {
            ioScope { model.request.toWcRequest(model.metadata).dispatch() }
        }
    }
}