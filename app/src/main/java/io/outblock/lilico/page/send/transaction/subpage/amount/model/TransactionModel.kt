package io.outblock.lilico.page.send.transaction.subpage.amount.model

import android.os.Parcelable
import io.outblock.lilico.network.model.AddressBookContact
import kotlinx.parcelize.Parcelize
import wallet.core.jni.CoinType

@Parcelize
class TransactionModel(
    val amount: Float,
    val coinType: Int = CoinType.FLOW.value(),
    val target: AddressBookContact,
    val fromAddress: String,
) : Parcelable