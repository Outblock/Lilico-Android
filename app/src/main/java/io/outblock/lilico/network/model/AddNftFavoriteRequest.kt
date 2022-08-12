package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class AddNftFavoriteRequest(
    @SerializedName("contract")
    val contractName: String,
    @SerializedName("ids")
    val tokenId: String,
)