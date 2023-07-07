package io.outblock.lilico.page.dialog.accounts.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAccountListDialogBinding
import io.outblock.lilico.manager.account.Account
import io.outblock.lilico.manager.account.AccountManager
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.loadAvatar

class AccountListAdapter : BaseAdapter<Account>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountViewHolder(parent.inflate(R.layout.item_account_list_dialog))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AccountViewHolder).bind(getItem(position))
    }
}

private class AccountViewHolder(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<Account> {

    private val binding by lazy { ItemAccountListDialogBinding.bind(view) }

    private var model: Account? = null

    init {
        view.setOnClickListener {
            model?.let { AccountManager.switch(it) }
        }
    }

    override fun bind(model: Account) {
        this.model = model
        with(binding) {
            val userInfo = model.userInfo
            avatarView.loadAvatar(userInfo.avatar)
            usernameView.text = userInfo.username
            addressView.text = model.wallet?.walletAddress()
            checkedView.setVisible(model.isActive)
        }
    }
}
