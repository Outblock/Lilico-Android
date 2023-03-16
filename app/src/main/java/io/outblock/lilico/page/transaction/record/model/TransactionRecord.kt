package io.outblock.lilico.page.transaction.record.model

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.network.flowscan.model.FlowScanTransaction

data class TransactionRecord(
    val transaction: FlowScanTransaction
)

data class TransactionRecordList(
    @SerializedName("list")
    val list: List<FlowScanTransaction>
)