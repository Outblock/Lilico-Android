package io.outblock.lilico.network.flowscan.model


import com.google.gson.annotations.SerializedName

data class FlowScanAccountTransferCountModel(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("data")
        val data: Data?
    ) {
        data class Data(
            @SerializedName("account")
            val account: Account?
        ) {
            data class Account(
                @SerializedName("transactionCount")
                val transactionCount: Int?
            )
        }
    }
}