package io.outblock.lilico.page.transaction

import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.network.flowscan.model.FlowScanTransaction
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import org.joda.time.format.ISODateTimeFormat


fun TransactionState.toTransactionRecord(): TransactionRecord {
    return TransactionRecord(
        transaction = FlowScanTransaction(
            hash = transactionId,
            time = ISODateTimeFormat.dateTime().print(time),
            status = state.stateToString(),
            error = errorMsg,
        )
    )
}

private fun Int.stateToString(): String {
    return when (this) {
        FlowTransactionStatus.UNKNOWN.num -> "Unknown"
        FlowTransactionStatus.PENDING.num -> "Pending"
        FlowTransactionStatus.FINALIZED.num -> "Finalized"
        FlowTransactionStatus.EXECUTED.num -> "Executed"
        FlowTransactionStatus.SEALED.num -> "Sealed"
        FlowTransactionStatus.EXPIRED.num -> "Expired"
        else -> ""
    }
}