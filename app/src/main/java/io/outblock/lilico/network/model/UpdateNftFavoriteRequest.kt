package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class UpdateNftFavoriteRequest(
    @SerializedName("ids")
    val ids: String,
)