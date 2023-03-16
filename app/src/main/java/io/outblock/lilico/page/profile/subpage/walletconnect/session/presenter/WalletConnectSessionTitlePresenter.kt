package io.outblock.lilico.page.profile.subpage.walletconnect.session.presenter

import android.view.View
import android.widget.TextView
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.profile.subpage.walletconnect.session.model.WalletConnectSessionTitleModel

class WalletConnectSessionTitlePresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<WalletConnectSessionTitleModel> {

    override fun bind(model: WalletConnectSessionTitleModel) {
        (view as TextView).text = model.title
    }
}