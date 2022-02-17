package io.outblock.lilico.page.send

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.addressBookCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.utils.viewModelIOScope

class TransactionSendViewModel : ViewModel() {

    val recentListLiveData = MutableLiveData<List<Any>>()
    val addressListLiveData = MutableLiveData<List<Any>>()
    val accountListLiveData = MutableLiveData<List<Any>>()

    fun load(type: Int) {
        when (type) {
            AddressPageFragment.TYPE_RECENT -> loadRecent()
            AddressPageFragment.TYPE_ADDRESS -> loadAddressBook()
            AddressPageFragment.TYPE_ACCOUNT -> loadAccounts()
        }
    }

    private fun loadRecent() {

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

    }

    private fun List<AddressBookContact>.format() = this.sortedBy { it.name() }.map { AddressBookPersonModel(data = it) }
}