package io.outblock.lilico.page.send.subpage.amount

import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.model.AddressBookContact

class SendAmountViewModel : ViewModel() {
    private lateinit var contact: AddressBookContact

    fun setContact(contact: AddressBookContact) {
        this.contact = contact
    }
}