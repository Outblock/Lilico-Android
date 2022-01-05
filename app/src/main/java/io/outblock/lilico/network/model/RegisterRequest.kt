package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("account_key")
    val accountKey: AccountKey,
)