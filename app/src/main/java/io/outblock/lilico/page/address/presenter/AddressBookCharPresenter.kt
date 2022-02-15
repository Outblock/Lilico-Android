package io.outblock.lilico.page.address.presenter

import android.view.View
import android.widget.TextView
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.address.model.AddressBookCharModel

class AddressBookCharPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<AddressBookCharModel> {

    override fun bind(model: AddressBookCharModel) {
        (view as? TextView)?.text = model.text
    }
}