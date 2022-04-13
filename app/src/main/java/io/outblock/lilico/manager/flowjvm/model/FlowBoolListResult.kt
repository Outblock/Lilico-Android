package io.outblock.lilico.manager.flowjvm.model


import com.google.gson.annotations.SerializedName

data class FlowBoolListResult(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: List<Value>
) {
    data class Value(
        @SerializedName("type")
        val type: String,
        @SerializedName("value")
        val value: Boolean
    )
}