package io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.presenter

import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityChildAccountEditBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.ChildAccountEditActivity
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.model.ChildAccountEditModel
import io.outblock.lilico.utils.loadAvatar

class ChildAccountEditPresenter(
    private val binding: ActivityChildAccountEditBinding,
    private val activity: ChildAccountEditActivity,
) : BasePresenter<ChildAccountEditModel> {


    init {
    }

    override fun bind(model: ChildAccountEditModel) {
        model.account?.let { updateAccount(it) }
    }

    private fun updateAccount(account: ChildAccount) {
        with(binding) {
            avatarView.loadAvatar(account.icon)
            nameEditTextView.setText(account.name)
            descriptionEditTextView.setText(account.description)
        }
    }

}