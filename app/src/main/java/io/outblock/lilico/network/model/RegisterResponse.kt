package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("custom_token")
    val customToken: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("user_id")
    val uid: String,
)