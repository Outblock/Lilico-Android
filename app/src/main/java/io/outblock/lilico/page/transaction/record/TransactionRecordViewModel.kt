package io.outblock.lilico.page.transaction.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.flowscan.flowScanAccountTransferQuery
import io.outblock.lilico.network.flowscan.flowScanTokenTransferQuery
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.utils.viewModelIOScope

class TransactionRecordViewModel : ViewModel() {
    private var contractId: String? = null

    val transactionListLiveData = MutableLiveData<List<TransactionRecord>>()

    fun setContractId(contractId: String?) {
        this.contractId = contractId
    }

    fun load() {
        viewModelIOScope(this) {
            val data = if (isQueryByToken()) {
                flowScanTokenTransferQuery(contractId!!)
            } else {
                flowScanAccountTransferQuery()
            }
            data?.let { transactionListLiveData.postValue(it) }
        }
    }

    private fun isQueryByToken() = contractId != null
}