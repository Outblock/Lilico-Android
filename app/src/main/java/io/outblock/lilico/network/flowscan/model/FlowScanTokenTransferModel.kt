package io.outblock.lilico.network.flowscan.model


import com.google.gson.annotations.SerializedName

data class FlowScanTokenTransferModel(
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
                @SerializedName("tokenTransfers")
                val tokenTransfers: TokenTransfers?
            ) {
                data class TokenTransfers(
                    @SerializedName("edges")
                    val edges: List<Edge?>?,
                    @SerializedName("pageInfo")
                    val pageInfo: PageInfo?
                ) {
                    data class Edge(
                        @SerializedName("node")
                        val node: Node?
                    ) {
                        data class Node(
                            @SerializedName("amount")
                            val amount: Amount?,
                            @SerializedName("counterpartiesCount")
                            val counterpartiesCount: Int?,
                            @SerializedName("counterparty")
                            val counterparty: Counterparty?,
                            @SerializedName("transaction")
                            val transaction: FlowScanTransaction?,
                            @SerializedName("type")
                            val type: String?
                        ) {
                            data class Amount(
                                @SerializedName("token")
                                val token: Token?,
                                @SerializedName("value")
                                val value: String?
                            ) {
                                data class Token(
                                    @SerializedName("id")
                                    val id: String?
                                )
                            }

                            data class Counterparty(
                                @SerializedName("address")
                                val address: String?
                            )


                        }
                    }

                    data class PageInfo(
                        @SerializedName("endCursor")
                        val endCursor: String?,
                        @SerializedName("hasNextPage")
                        val hasNextPage: Boolean?
                    )
                }
            }
        }
    }
}