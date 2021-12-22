package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class UsernameCheckResponse(
    @SerializedName("data")
    val data: UsernameCheckData,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class UsernameCheckData(
    @SerializedName("user_name")
    var username: String,

    @SerializedName("unique")
    val unique: Boolean,
)