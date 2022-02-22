package io.outblock.lilico.page.send.subpage.amount.model

import io.outblock.lilico.network.model.AddressBookContact
import wallet.core.jni.CoinType

class TransactionModel(
    val amount: Float,
    val coinType: Int = CoinType.FLOW.value(),
    val target: AddressBookContact,
    val fromAddress: String,
)