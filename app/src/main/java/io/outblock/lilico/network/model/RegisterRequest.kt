package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("user_name")
    val username: String,

    @SerializedName("account_key")
    val accountKey: AccountKey,
)