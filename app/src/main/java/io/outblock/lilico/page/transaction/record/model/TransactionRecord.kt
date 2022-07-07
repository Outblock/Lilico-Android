package io.outblock.lilico.page.transaction.record.model

import io.outblock.lilico.network.flowscan.model.FlowScanTransaction

data class TransactionRecord(
    val transaction: FlowScanTransaction
)
