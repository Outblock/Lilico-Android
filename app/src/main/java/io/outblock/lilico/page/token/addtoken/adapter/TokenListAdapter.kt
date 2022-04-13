package io.outblock.lilico.page.token.addtoken.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.token.addtoken.model.TokenItem
import io.outblock.lilico.page.token.addtoken.presenter.TokenItemPresenter
import io.outblock.lilico.page.token.addtoken.tokenListDiffCallback

class TokenListAdapter : BaseAdapter<Any>(tokenListDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return TYPE_TOKEN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TOKEN -> TokenItemPresenter(parent.inflate(R.layout.item_token_list))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TokenItemPresenter -> holder.bind(getItem(position) as TokenItem)
        }
    }

    companion object {
        private const val TYPE_TOKEN = 1
        private const val TYPE_LETTER = 2
    }
}