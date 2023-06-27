package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.presenter

import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityChildAccountDetailBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.ChildAccountDetailActivity
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.dialog.ChildAccountUnlinkDialog
import io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail.model.ChildAccountDetailModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.textToClipboard
import io.outblock.lilico.utils.toast

class ChildAccountDetailPresenter(
    private val binding: ActivityChildAccountDetailBinding,
    private val activity: ChildAccountDetailActivity,
) : BasePresenter<ChildAccountDetailModel> {


    init {
    }

    override fun bind(model: ChildAccountDetailModel) {
        model.account?.let { updateAccount(it) }
    }

    private fun updateAccount(account: ChildAccount) {
        with(binding) {
            logoView.loadAvatar(account.icon)
            nameView.text = account.name
            addressView.text = account.address

            descriptionView.text = account.description
            descriptionTitleView.setVisible(!account.description.isNullOrEmpty())

            addressCopyButton.setOnClickListener {
                textToClipboard(account.address)
                toast(msgRes = R.string.copy_address_toast)
            }

            unlinkButton.setOnClickListener { ChildAccountUnlinkDialog.show(activity.supportFragmentManager, account) }
        }
    }

}