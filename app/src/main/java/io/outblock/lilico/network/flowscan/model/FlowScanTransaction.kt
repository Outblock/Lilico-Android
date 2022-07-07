package io.outblock.lilico.network.flowscan.model

import com.google.gson.annotations.SerializedName

data class FlowScanTransaction(
    @SerializedName("authorizers")
    val authorizers: List<FlowScanAccount>?,
    @SerializedName("contractInteractions")
    val contractInteractions: List<ContractInteraction?>?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("eventCount")
    val eventCount: Int?,
    @SerializedName("hash")
    val hash: String?,
    @SerializedName("index")
    val index: Int?,
    @SerializedName("payer")
    val payer: FlowScanAccount?,
    @SerializedName("proposer")
    val proposer: FlowScanAccount?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("time")
    val time: String?
) {
    data class ContractInteraction(
        @SerializedName("identifier")
        val identifier: String?
    )
}

data class FlowScanAccount(
    @SerializedName("address")
    val address: String?
)