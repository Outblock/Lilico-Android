package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class CadenceSecurityCheck(
    @SerializedName("cadence_base64")
    val cadenceBase64: String,
    @SerializedName("network")
    val network: String,
)