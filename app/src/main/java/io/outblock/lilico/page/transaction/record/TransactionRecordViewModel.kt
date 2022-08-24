package io.outblock.lilico.page.transaction.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.flowscan.flowScanAccountTransferCountQuery
import io.outblock.lilico.network.flowscan.flowScanAccountTransferQuery
import io.outblock.lilico.network.flowscan.flowScanTokenTransferQuery
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.page.transaction.toTransactionRecord
import io.outblock.lilico.utils.viewModelIOScope

class TransactionRecordViewModel : ViewModel(), OnTransactionStateChange {
    private var contractId: String? = null

    val transactionCountLiveData = MutableLiveData<Int>()
    val transferCountLiveData = MutableLiveData<Int>()

    val transactionListLiveData = MutableLiveData<List<TransactionRecord>>()
    val transferListLiveData = MutableLiveData<List<Any>>()

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
        viewModelIOScope(this) { loadTransaction() }
        viewModelIOScope(this) { loadTransfer() }
    }

    private suspend fun loadTransaction() {
        val data = if (isQueryByToken()) {
            flowScanTokenTransferQuery(contractId!!)
        } else {
            flowScanAccountTransferQuery()
        }

        val processing = TransactionStateManager.getProcessingTransaction().map { it.toTransactionRecord() }

        transactionCountLiveData.postValue(flowScanAccountTransferCountQuery() + processing.size)

        transactionListLiveData.postValue((processing + data.orEmpty()))
    }

    private suspend fun loadTransfer() {
        val service = retrofit().create(ApiService::class.java)
        val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return
        val resp = if (isQueryByToken()) {
            service.getTransferRecordByToken(walletAddress, contractId!!)
        } else {
            service.getTransferRecord(walletAddress)
        }

        transferListLiveData.postValue(resp.data?.transactions.orEmpty())
        transferCountLiveData.postValue(resp.data?.total ?: 0)
    }

    private fun isQueryByToken() = contractId != null
}