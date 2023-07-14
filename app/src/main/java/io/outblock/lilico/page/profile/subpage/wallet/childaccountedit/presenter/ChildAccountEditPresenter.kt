package io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.presenter

import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityChildAccountEditBinding
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.ChildAccountEditActivity
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.ChildAccountEditViewModel
import io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.model.ChildAccountEditModel
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.startGallery
import io.outblock.lilico.widgets.ProgressDialog

class ChildAccountEditPresenter(
    private val binding: ActivityChildAccountEditBinding,
    private val activity: ChildAccountEditActivity,
) : BasePresenter<ChildAccountEditModel> {

    private var progressDialog: ProgressDialog? = null
    private val viewModel by lazy { ViewModelProvider(activity)[ChildAccountEditViewModel::class.java] }

    init {
        with(binding) {
            avatarContainer.setOnClickListener { startGallery(activity) }
            saveButton.setOnClickListener { viewModel.save(nameEditTextView.text(), descriptionEditTextView.text()) }
        }
    }

    override fun bind(model: ChildAccountEditModel) {
        model.account?.let { updateAccount(it) }
        model.avatarFile?.let { Glide.with(binding.avatarView).load(it).into(binding.avatarView) }
        model.showProgressDialog?.let { toggleProgressDialog(it) }
    }

    private fun updateAccount(account: ChildAccount) {
        with(binding) {
            avatarView.loadAvatar(account.icon)
            nameEditTextView.setText(account.name)
            descriptionEditTextView.setText(account.description)
        }
    }

    private fun EditText.text() = text?.toString().orEmpty()

    private fun toggleProgressDialog(isVisible: Boolean) {
        if (isVisible) {
            progressDialog = ProgressDialog(activity, cancelable = false)
            progressDialog?.show()
        } else {
            progressDialog?.dismiss()
        }
    }
}