package io.outblock.lilico.page.address

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.UserInfoData

val addressBookDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }
}

fun UserInfoData.toContact(): AddressBookContact {
    return AddressBookContact(
        address = address,
        username = username,
        avatar = avatar,
    )
}