package io.outblock.lilico.page.profile.subpage.wallet.childaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.wallet.WalletManager

class ChildAccountsViewModel : ViewModel() {

    val accountsLiveData = MutableLiveData<List<ChildAccount>>()

    fun load() {
        val childAccountList = WalletManager.childAccountList() ?: return
        accountsLiveData.postValue(childAccountList.get().map { it.copy() }.sortedByDescending { it.pinTime })
    }

    fun togglePinAccount(account: ChildAccount) {
        WalletManager.childAccountList()?.togglePin(account)
        load()
    }

}