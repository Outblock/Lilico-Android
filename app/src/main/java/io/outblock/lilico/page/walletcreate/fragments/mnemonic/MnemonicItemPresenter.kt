package io.outblock.lilico.page.walletcreate.fragments.mnemonic

import android.view.View
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.widgets.MnemonicItem

class MnemonicItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<MnemonicModel> {

    override fun bind(model: MnemonicModel) {
        (view as MnemonicItem).setText("${model.index}. ${model.text}")
    }
}