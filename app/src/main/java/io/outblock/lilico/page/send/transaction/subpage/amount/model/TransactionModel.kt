package io.outblock.lilico.page.send.transaction.subpage.amount.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.network.model.AddressBookContact
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionModel(
    @SerializedName("amount")
    val amount: Float,
    @SerializedName("coinSymbol")
    val coinSymbol: String = FlowCoin.SYMBOL_FLOW,
    @SerializedName("target")
    val target: AddressBookContact,
    @SerializedName("fromAddress")
    val fromAddress: String,
) : Parcelable