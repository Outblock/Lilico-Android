package io.outblock.lilico.page.staking.providers.presenter

import android.view.View
import android.widget.TextView
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.staking.providers.model.ProviderTitleModel

class ProviderTitlePresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<ProviderTitleModel> {

    override fun bind(model: ProviderTitleModel) {
        (view as? TextView)?.text = model.title
    }
}