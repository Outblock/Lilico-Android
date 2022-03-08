package io.outblock.lilico.page.send.nft

import android.os.Parcelable
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.Nft
import kotlinx.parcelize.Parcelize

@Parcelize
class NftSendModel(
    val nft: Nft,
    val target: AddressBookContact,
    val fromAddress: String,
) : Parcelable