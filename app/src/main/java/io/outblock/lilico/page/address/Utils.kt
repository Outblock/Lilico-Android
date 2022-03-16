package io.outblock.lilico.page.address

import androidx.recyclerview.widget.DiffUtil
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.address.model.AddressBookPersonModel

val addressBookDiffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is AddressBookPersonModel && newItem is AddressBookPersonModel) {
            return oldItem.data == newItem.data
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is AddressBookPersonModel && newItem is AddressBookPersonModel) {
            return oldItem.data == newItem.data && oldItem.isFriend == newItem.isFriend
        }
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

fun isAddressBookAutoSearch(text: CharSequence?): Boolean {
    if (text.isNullOrBlank()) {
        return false
    }
    return !text.trim().contains(" ") && (text.endsWith(".find") || text.endsWith(".fn"))
}

fun List<AddressBookContact>.removeRepeated(): List<AddressBookContact> {
    return distinctBy { it.uniqueId() }
}