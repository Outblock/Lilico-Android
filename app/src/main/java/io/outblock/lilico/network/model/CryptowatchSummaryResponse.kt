package io.outblock.lilico.network.model


import com.google.gson.annotations.SerializedName

data class CryptowatchSummaryResponse(
    @SerializedName("allowance")
    val allowance: Allowance,
    @SerializedName("result")
    val result: Result
) {
    data class Allowance(
        @SerializedName("cost")
        val cost: Float,
        @SerializedName("remaining")
        val remaining: Float,
    )

    data class Result(
        @SerializedName("price")
        val price: Price,
        @SerializedName("volume")
        val volume: Float,
        @SerializedName("volumeQuote")
        val volumeQuote: Float
    ) {
        data class Price(
            @SerializedName("change")
            val change: Change,
            @SerializedName("high")
            val high: Float,
            @SerializedName("last")
            val last: Float,
            @SerializedName("low")
            val low: Float
        ) {
            data class Change(
                @SerializedName("absolute")
                val absolute: Float,
                @SerializedName("percentage")
                val percentage: Float
            )
        }
    }
}