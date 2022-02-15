package io.outblock.lilico.page.address.presenter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemAddressBookPersonBinding
import io.outblock.lilico.network.model.AddressBookDomain
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.loadAvatar

class AddressBookPersonPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<AddressBookPersonModel> {

    private val binding by lazy { ItemAddressBookPersonBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: AddressBookPersonModel) {
        val data = model.data
        with(binding) {
            nameView.text = "${data.name()} ${if (!data.username.isNullOrEmpty()) "  (${data.username})" else ""}"

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
        }
    }
}