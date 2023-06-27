package io.outblock.lilico.page.profile.subpage.wallet.childaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.childaccount.ChildAccountList
import io.outblock.lilico.manager.childaccount.ChildAccountUpdateListenerCallback
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.manager.wallet.WalletManager

class ChildAccountsViewModel : ViewModel(), OnTransactionStateChange, ChildAccountUpdateListenerCallback {

    val accountsLiveData = MutableLiveData<List<ChildAccount>>()

    init {
        TransactionStateManager.addOnTransactionStateChange(this)
        ChildAccountList.addAccountUpdateListener(this)
    }

    fun load() {
        val childAccountList = WalletManager.childAccountList() ?: return
        accountsLiveData.postValue(childAccountList.get().map { it.copy() }.sortedByDescending { it.pinTime })
    }

    fun togglePinAccount(account: ChildAccount) {
        WalletManager.childAccountList()?.togglePin(account)
        load()
    }

    override fun onTransactionStateChange() {
        val state = TransactionStateManager.getLastVisibleTransaction() ?: return
        if (state.isSuccess()) {
            WalletManager.refreshChildAccount()
        }
    }

    override fun onChildAccountUpdate(parentAddress: String, accounts: List<ChildAccount>) {
        load()
    }
}