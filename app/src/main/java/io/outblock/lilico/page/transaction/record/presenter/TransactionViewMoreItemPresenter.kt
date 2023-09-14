package io.outblock.lilico.page.transaction.record.presenter

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.transaction.record.model.TransactionViewMoreModel
import io.outblock.lilico.utils.findActivity

class TransactionViewMoreItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<TransactionViewMoreModel> {

    override fun bind(model: TransactionViewMoreModel) {
        view.setOnClickListener { openBrowser(findActivity(view)!!, model.url()) }
    }

    private fun TransactionViewMoreModel.url(): String {
        return if (isTestnet()) {
            "https://testnet.flowdiver.io/account/$address"
        } else "https://flowdiver.io/account/$address"
    }
}