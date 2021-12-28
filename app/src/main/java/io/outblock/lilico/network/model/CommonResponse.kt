package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)