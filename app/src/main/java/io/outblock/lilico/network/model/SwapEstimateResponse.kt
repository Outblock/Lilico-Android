package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class SwapEstimateResponse(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("priceImpact")
        val priceImpact: String?,
        @SerializedName("routes")
        val routes: List<Route?>?,
        @SerializedName("tokenInAmount")
        val tokenInAmount: Float?,
        @SerializedName("tokenInKey")
        val tokenInKey: String?,
        @SerializedName("tokenOutAmount")
        val tokenOutAmount: Float?,
        @SerializedName("tokenOutKey")
        val tokenOutKey: String?
    ) {
        data class Route(
            @SerializedName("route")
            val route: List<String?>?,
            @SerializedName("routeAmountIn")
            val routeAmountIn: Float?,
            @SerializedName("routeAmountOut")
            val routeAmountOut: Float?
        )
    }
}