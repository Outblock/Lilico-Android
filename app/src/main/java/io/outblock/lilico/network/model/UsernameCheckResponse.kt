package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class UsernameCheckResponse(

    @SerializedName("user_name")
    var username: String,

    @SerializedName("unique")
    val unique: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)