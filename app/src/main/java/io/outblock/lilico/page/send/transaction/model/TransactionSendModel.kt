package io.outblock.lilico.page.send.transaction.model

import io.outblock.lilico.network.model.AddressBookContact

class TransactionSendModel(
    val qrcode: String? = null,
    val selectedAddress: AddressBookContact? = null,
    val isClearInputFocus: Boolean? = null,
)