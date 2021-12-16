package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class AccountKey(
    @SerializedName("hash_algo")
    val hashAlgo: Int = 1,

    @SerializedName("sign_algo")
    val signAlgo: Int = 1,

    @SerializedName("weight")
    val weight: Int = 1000,

    @SerializedName("public_key")
    val publicKey: String,
)