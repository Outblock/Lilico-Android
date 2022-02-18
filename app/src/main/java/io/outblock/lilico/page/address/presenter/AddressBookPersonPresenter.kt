package io.outblock.lilico.page.address.presenter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAddressBookPersonBinding
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.AddressBookDomain
import io.outblock.lilico.page.address.AddressBookViewModel
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.addressadd.AddressAddActivity
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.loadAvatar

class AddressBookPersonPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<AddressBookPersonModel> {

    private val binding by lazy { ItemAddressBookPersonBinding.bind(view) }

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[AddressBookViewModel::class.java] }

    @SuppressLint("SetTextI18n")
    override fun bind(model: AddressBookPersonModel) {
        val data = model.data
        with(binding) {
            nameView.text = "${data.name()} ${if (!data.username.isNullOrEmpty()) "  (@${data.username})" else ""}"

            namePrefixView.text = data.prefixName()
            namePrefixView.setVisible(data.prefixName().isNotEmpty())

            if (data.domain?.domainType ?: 0 == 0) {
                avatarView.setVisible(!data.avatar.isNullOrEmpty(), invisible = true)
                avatarView.loadAvatar(data.avatar.orEmpty())
            } else {
                val avatar =
                    if (data.domain?.domainType == AddressBookDomain.DOMAIN_FIND_XYZ) R.drawable.ic_domain_logo_findxyz else R.drawable.ic_domain_logo_flowns
                avatarView.setVisible(true)
                Glide.with(avatarView).load(avatar).into(avatarView)
            }

            addressView.text = data.address.orEmpty()

            addButton.setVisible(!viewModel.isAddressBookContains(model))

            addButton.setOnClickListener { viewModel.addFriend(model.data) }
        }

        view.setOnClickListener {
            if (viewModel.isAddressBookContains(model)) {
                AddressActionDialog(findActivity(view) as FragmentActivity, data).show()
            }
        }
    }
}

private class AddressActionDialog(
    private val context: FragmentActivity,
    private val contact: AddressBookContact,
) {

    private val viewModel by lazy { ViewModelProvider(context)[AddressBookViewModel::class.java] }

    fun show() {
        var dialog: Dialog? = null
        with(AlertDialog.Builder(context, R.style.Theme_AlertDialogTheme)) {
            setAdapter(ArrayAdapter<String>(context, R.layout.item_address_book_action).apply {
                if (!contact.contactName.isNullOrBlank()) {
                    add(R.string.edit.res2String())
                }
                add(R.string.delete.res2String())
            }) { dialog, which ->
                dialog.dismiss()
                if (which == 0) AddressAddActivity.launch(context, contact) else viewModel.delete(contact)
            }
            with(create()) {
                dialog = this
                show()
            }
        }
    }
}