package io.outblock.lilico.page.transaction.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.transactionRecordCache
import io.outblock.lilico.cache.transferRecordCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.flowscan.flowScanAccountTransferCountQuery
import io.outblock.lilico.network.flowscan.flowScanAccountTransferQuery
import io.outblock.lilico.network.flowscan.flowScanTokenTransferQuery
import io.outblock.lilico.network.model.TransferRecordList
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.page.transaction.record.model.TransactionRecordList
import io.outblock.lilico.page.transaction.toTransactionRecord
import io.outblock.lilico.utils.*

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
        transactionCountLiveData.postValue(getAccountTransactionCountLocal())
        transactionListLiveData.postValue(transactionRecordCache().read()?.list.orEmpty().map { TransactionRecord(it) })

        val query = if (isQueryByToken()) {
            flowScanTokenTransferQuery(contractId!!)
        } else {
            flowScanAccountTransferQuery()
        }

        val processing = TransactionStateManager.getProcessingTransaction().map { it.toTransactionRecord() }
        val data = processing + query.orEmpty()

        transactionCountLiveData.postValue(flowScanAccountTransferCountQuery() + processing.size)
        transactionListLiveData.postValue(data)

        transactionRecordCache().cache(TransactionRecordList(data.map { it.transaction }))


        updateAccountTransactionCountLocal(transactionCountLiveData.value ?: 0)
    }

    private suspend fun loadTransfer() {
        transferListLiveData.postValue(transferRecordCache(contractId.orEmpty()).read()?.list.orEmpty())
        transferCountLiveData.postValue(getAccountTransferCount())

        val service = retrofit().create(ApiService::class.java)
        val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return
        val resp = if (isQueryByToken()) {
            service.getTransferRecordByToken(walletAddress, contractId!!)
        } else {
            service.getTransferRecord(walletAddress)
        }
        val data = resp.data?.transactions.orEmpty()
        transferListLiveData.postValue(data)
        transferCountLiveData.postValue(resp.data?.total ?: 0)

        transferRecordCache(contractId.orEmpty()).cache(TransferRecordList(data))
        updateAccountTransferCount(resp.data?.total ?: 0)
    }

    private fun isQueryByToken() = contractId != null
}