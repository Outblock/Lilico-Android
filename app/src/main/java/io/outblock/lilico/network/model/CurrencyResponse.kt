package io.outblock.lilico.network.model


import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("date")
    val date: String,
    @SerializedName("historical")
    val historical: Boolean,
    @SerializedName("result")
    val result: Float,
    @SerializedName("success")
    val success: Boolean
)