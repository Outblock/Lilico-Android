package io.outblock.lilico.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SwapEstimateResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {

    @Parcelize
    data class Data(
        @SerializedName("priceImpact")
        val priceImpact: Float,
        @SerializedName("routes")
        val routes: List<Route?>,
        @SerializedName("tokenInAmount")
        val tokenInAmount: Float,
        @SerializedName("tokenInKey")
        val tokenInKey: String,
        @SerializedName("tokenOutAmount")
        val tokenOutAmount: Float,
        @SerializedName("tokenOutKey")
        val tokenOutKey: String
    ) : Parcelable {

        @Parcelize
        data class Route(
            @SerializedName("route")
            val route: List<String>,
            @SerializedName("routeAmountIn")
            val routeAmountIn: Float,
            @SerializedName("routeAmountOut")
            val routeAmountOut: Float
        ) : Parcelable
    }
}