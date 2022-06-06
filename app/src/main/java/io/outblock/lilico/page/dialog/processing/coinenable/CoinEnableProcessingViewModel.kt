package io.outblock.lilico.page.dialog.processing.coinenable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager

class CoinEnableProcessingViewModel : ViewModel(), OnTransactionStateChange {
    val stateChangeLiveData = MutableLiveData<TransactionState>()

    lateinit var state: TransactionState

    fun bindTransactionState(state: TransactionState) {
        this.state = state
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    override fun onTransactionStateChange() {
        val state = TransactionStateManager.getLastVisibleTransaction() ?: return
        if (state.transactionId != this.state.transactionId) {
            return
        }
        stateChangeLiveData.postValue(state)
    }
}