package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.HashAlgorithm
import com.nftco.flow.sdk.SignatureAlgorithm

data class AccountKey(
    @SerializedName("hash_algo")
    val hashAlgo: Int = HashAlgorithm.SHA3_256.index,

    @SerializedName("sign_algo")
    val signAlgo: Int = SignatureAlgorithm.ECDSA_SECP256k1.index,

    @SerializedName("weight")
    val weight: Int = 1000,

    @SerializedName("public_key")
    val publicKey: String,
)