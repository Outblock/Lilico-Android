package io.outblock.lilico.page.dialog.accounts.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAccountListDialogBinding
import io.outblock.lilico.page.dialog.accounts.model.AccountModel
import io.outblock.lilico.utils.loadAvatar

class AccountListAdapter : BaseAdapter<AccountModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountViewHolder(parent.inflate(R.layout.item_account_list_dialog))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AccountViewHolder).bind(getItem(position))
    }
}

private class AccountViewHolder(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<AccountModel> {

    private val binding by lazy { ItemAccountListDialogBinding.bind(view) }

    init {
        view.setOnClickListener {
        }
    }

    override fun bind(model: AccountModel) {
        with(binding) {
            avatarView.loadAvatar(model.avatar)
            usernameView.text = model.username
            addressView.text = model.address
//            checkedView.setVisible()
        }
    }
}
