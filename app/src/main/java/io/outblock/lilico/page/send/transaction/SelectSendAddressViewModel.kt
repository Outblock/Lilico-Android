package io.outblock.lilico.page.send.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.addressBookCache
import io.outblock.lilico.cache.recentTransactionCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.utils.viewModelIOScope

class SelectSendAddressViewModel : ViewModel() {

    val recentListLiveData = MutableLiveData<List<Any>>()
    val addressListLiveData = MutableLiveData<List<Any>>()
    val accountListLiveData = MutableLiveData<List<Any>>()

    val onAddressSelectedLiveData = MutableLiveData<AddressBookContact>()

    fun load(type: Int) {
        when (type) {
            AddressPageFragment.TYPE_RECENT -> loadRecent()
            AddressPageFragment.TYPE_ADDRESS -> loadAddressBook()
            AddressPageFragment.TYPE_ACCOUNT -> loadAccounts()
        }
    }

    private fun loadRecent() {
        viewModelIOScope(this) {
            recentTransactionCache().read()?.let { data ->
                recentListLiveData.postValue(data.contacts?.map { AddressBookPersonModel(data = it) }.orEmpty())
            }
        }
    }

    private fun loadAddressBook() {
        viewModelIOScope(this) {
            addressBookCache().read()?.contacts?.let { data -> addressListLiveData.postValue(data.format()) }

            val service = retrofit().create(ApiService::class.java)
            val resp = service.getAddressBook()
            resp.data.contacts?.let { data ->
                addressListLiveData.postValue(data.format())
                addressBookCache().cache(resp.data)
            }
        }
    }

    private fun loadAccounts() {
        viewModelIOScope(this) {

        }
    }

    private fun List<AddressBookContact>.format() = this.sortedBy { it.name() }.map { AddressBookPersonModel(data = it) }
}