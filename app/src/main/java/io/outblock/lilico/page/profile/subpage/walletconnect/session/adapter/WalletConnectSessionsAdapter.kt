package io.outblock.lilico.page.profile.subpage.walletconnect.session.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.walletconnect.sign.client.Sign
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.profile.subpage.walletconnect.session.model.WalletConnectSessionTitleModel
import io.outblock.lilico.page.profile.subpage.walletconnect.session.presenter.WalletConnectSessionItemPresenter
import io.outblock.lilico.page.profile.subpage.walletconnect.session.presenter.WalletConnectSessionTitlePresenter

class WalletConnectSessionsAdapter : BaseAdapter<Any>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Sign.Model.Session -> TYPE_SESSION
            is WalletConnectSessionTitleModel -> TYPE_TITLE
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SESSION -> WalletConnectSessionItemPresenter(parent.inflate(R.layout.item_wallet_connect_session))
            TYPE_TITLE -> WalletConnectSessionTitlePresenter(parent.inflate(R.layout.item_wallet_connect_session_title))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WalletConnectSessionItemPresenter -> holder.bind(getItem(position) as Sign.Model.Session)
            is WalletConnectSessionTitlePresenter -> holder.bind(getItem(position) as WalletConnectSessionTitleModel)
        }
    }

    companion object {
        private const val TYPE_SESSION = 1
        private const val TYPE_TITLE = 2
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Sign.Model.Session && newItem is Sign.Model.Session) {
            return oldItem.topic == newItem.topic
        }
        return true
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is Sign.Model.Session && newItem is Sign.Model.Session) {
            return oldItem.topic == newItem.topic
        }
        return true
    }
}