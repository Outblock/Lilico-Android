package io.outblock.lilico.page.profile.subpage.wallet.account.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemChildAccountListBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.account.ChildAccountsViewModel
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.ChildAccountDetailActivity
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.loadAvatar

class ChildAccountListAdapter : BaseAdapter<ChildAccount>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountViewHolder(parent.inflate(R.layout.item_child_account_list))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AccountViewHolder).bind(getItem(position))
    }
}

private class AccountViewHolder(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<ChildAccount> {
    private val binding by lazy { ItemChildAccountListBinding.bind(view) }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[ChildAccountsViewModel::class.java] }

    private var model: ChildAccount? = null

    init {
        view.setOnClickListener {
            model?.let { ChildAccountDetailActivity.launch(view.context, it) }
        }

        binding.pinButton.setOnClickListener { model?.let { viewModel.togglePinAccount(it) } }
    }

    override fun bind(model: ChildAccount) {
        this.model = model
        with(binding) {
            iconView.loadAvatar(model.icon)
            nameView.text = model.name.ifBlank { R.string.default_child_account_name.res2String() }
            addressView.text = model.address
            pinButton.imageTintList =
                ColorStateList.valueOf(if (model.pinTime > 0) R.color.salmon_primary.res2color() else R.color.neutrals9.res2color())
            pinButton.setBackgroundResource(if (model.pinTime > 0) R.drawable.bg_child_account_pin else R.color.transparent)
        }
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<ChildAccount>() {
    override fun areItemsTheSame(oldItem: ChildAccount, newItem: ChildAccount): Boolean {
        return oldItem.address == newItem.address
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ChildAccount, newItem: ChildAccount): Boolean {
        return oldItem.pinTime == newItem.pinTime && oldItem.address == newItem.address
    }
}