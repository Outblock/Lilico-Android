package io.outblock.lilico.manager.transaction

import com.nftco.flow.sdk.FlowId
import com.nftco.flow.sdk.FlowTransactionResult
import com.nftco.flow.sdk.FlowTransactionStatus
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.safeRun
import kotlinx.coroutines.delay

private val TAG = TransactionStateWatcher::class.java.simpleName

class TransactionStateWatcher(
    val transactionId: String,
) {

    suspend fun watch(callback: (state: FlowTransactionResult) -> Unit) {
        var ret: FlowTransactionResult? = null
        var statusCode = -1
        while (true) {
            safeRun {
                val result = checkNotNull(
                    FlowApi.get().getTransactionResultById(FlowId.of(transactionId.hexToBytes()))
                ) { "Transaction with that id not found" }
                logd(TAG, "statusCode:${result.status.num}")
                if (result.status.num != statusCode) {
                    statusCode = result.status.num
                    callback.invoke(result)
                }
                ret = result
            }

            if (!statusCode.isProcessing() || !ret?.errorMessage.isNullOrBlank()) {
                break
            }

            delay(500)
        }
    }

    private fun Int.isProcessing() = this < FlowTransactionStatus.SEALED.num && this >= FlowTransactionStatus.UNKNOWN.num

}