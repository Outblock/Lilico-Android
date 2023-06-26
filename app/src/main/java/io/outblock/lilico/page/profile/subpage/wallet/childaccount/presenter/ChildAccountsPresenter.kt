package io.outblock.lilico.page.profile.subpage.wallet.childaccount.presenter

import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityChildAccountsBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.page.profile.subpage.wallet.WalletSettingActivity
import io.outblock.lilico.page.profile.subpage.wallet.childaccount.ChildAccountsActivity
import io.outblock.lilico.page.profile.subpage.wallet.childaccount.adapter.ChildAccountListAdapter
import io.outblock.lilico.page.profile.subpage.wallet.childaccount.model.ChildAccountsModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class ChildAccountsPresenter(
    private val binding: ActivityChildAccountsBinding,
    private val activity: ChildAccountsActivity,
) : BasePresenter<ChildAccountsModel> {

    private val adapter by lazy { ChildAccountListAdapter() }

    init {
        with(binding.recyclerView) {
            adapter = this@ChildAccountsPresenter.adapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ColorDividerItemDecoration(R.color.transparent.res2color(), 8.dp2px().toInt()))
        }

        with(binding) {
            iconView.setImageResource(R.drawable.ic_coin_flow)
            nameView.setText(R.string.wallet)
            ioScope {
                val address = WalletManager.selectedWalletAddress() ?: return@ioScope
                uiScope { addressView.text = address }
            }

            headerWrapper.setOnClickListener { WalletSettingActivity.launch(activity) }
        }
    }

    override fun bind(model: ChildAccountsModel) {
        model.accounts?.let { updateAccounts(it) }
    }

    private fun updateAccounts(accounts: List<ChildAccount>) {
        with(binding) {
            listTitleView.setVisible(accounts.isNotEmpty())
            adapter.setNewDiffData(accounts)
        }
    }
}