package io.outblock.lilico.page.walletcreate.fragments.mnemonic

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.getItemView

class MnemonicAdapter : BaseAdapter<MnemonicModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MnemonicItemPresenter(parent.getItemView(R.layout.item_mnemonic))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as MnemonicItemPresenter).bind(item)
    }
}