package io.outblock.lilico.page.transaction.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.flowscan.flowScanAccountTransferQuery
import io.outblock.lilico.network.flowscan.flowScanTokenTransferQuery
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.page.transaction.toTransactionRecord
import io.outblock.lilico.utils.viewModelIOScope

class TransactionRecordViewModel : ViewModel(), OnTransactionStateChange {
    private var contractId: String? = null

    val transactionListLiveData = MutableLiveData<List<TransactionRecord>>()

    init {
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    fun setContractId(contractId: String?) {
        this.contractId = contractId
    }

    override fun onTransactionStateChange() {
        load()
    }

    fun load() {
        viewModelIOScope(this) {
            val data = if (isQueryByToken()) {
                flowScanTokenTransferQuery(contractId!!)
            } else {
                flowScanAccountTransferQuery()
            }

            val processing = TransactionStateManager.getProcessingTransaction().map { it.toTransactionRecord() }

            transactionListLiveData.postValue((processing + data.orEmpty()))
        }
    }

    private fun isQueryByToken() = contractId != null
}