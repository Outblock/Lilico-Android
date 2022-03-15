package io.outblock.lilico.page.send.nft

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.Nft
import kotlinx.parcelize.Parcelize

@Parcelize
class NftSendModel(
    @SerializedName("nft")
    val nft: Nft,
    @SerializedName("target")
    val target: AddressBookContact,
    @SerializedName("fromAddress")
    val fromAddress: String,
) : Parcelable