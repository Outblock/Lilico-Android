package io.outblock.lilico.page.send.transaction.subpage.amount.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.network.model.AddressBookContact
import kotlinx.parcelize.Parcelize
import wallet.core.jni.CoinType

@Parcelize
class TransactionModel(
    @SerializedName("amount")
    val amount: Float,
    @SerializedName("coinType")
    val coinType: Int = CoinType.FLOW.value(),
    @SerializedName("target")
    val target: AddressBookContact,
    @SerializedName("fromAddress")
    val fromAddress: String,
) : Parcelable