package io.outblock.lilico.network.flowscan.model


import com.google.gson.annotations.SerializedName

data class FlowScanAccountTransferModel(
    @SerializedName("data")
    val data: Data?
) {
    data class Data(
        @SerializedName("account")
        val account: Account?
    ) {
        data class Account(
            @SerializedName("transactionCount")
            val transactionCount: Int?,
            @SerializedName("transactions")
            val transactions: Transactions?
        ) {
            data class Transactions(
                @SerializedName("edges")
                val edges: List<Edge?>?
            ) {
                data class Edge(
                    @SerializedName("node")
                    val transaction: FlowScanTransaction?
                )
            }
        }
    }
}