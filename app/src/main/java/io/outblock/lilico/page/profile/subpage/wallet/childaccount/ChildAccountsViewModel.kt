package io.outblock.lilico.page.profile.subpage.wallet.childaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.childaccount.ChildAccountList

class ChildAccountsViewModel : ViewModel() {

    val accountsLiveData = MutableLiveData<List<ChildAccount>>()

    fun load() {
        accountsLiveData.postValue(ChildAccountList.get().map { it.copy() }.sortedByDescending { it.pinTime })
    }

    fun togglePinAccount(account: ChildAccount) {
        ChildAccountList.togglePin(account)
        load()
    }

}