package io.outblock.lilico.page.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.addressBookCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.address.model.AddressBookCharModel
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.utils.viewModelIOScope

class AddressBookViewModel : ViewModel() {
    val addressBookLiveData = MutableLiveData<List<Any>>()

    fun loadAddressBook() {
        viewModelIOScope(this) {
            addressBookCache().read()?.contacts?.let { addressBookLiveData.postValue(parseAddressBook(it)) }

            val service = retrofit().create(ApiService::class.java)
            val resp = service.getAddressBook()
            resp.data.contacts?.let {
                addressBookLiveData.postValue(parseAddressBook(it))
                addressBookCache().cache(resp.data)
            }
        }
    }

    private fun parseAddressBook(data: List<AddressBookContact>): List<Any> {
        val charList = mutableListOf<String>()
        return data.sortedBy { it.name() }.map {
            mutableListOf<Any>(
                AddressBookPersonModel(it)
            ).apply {
                if (!charList.contains(it.prefixName())) {
                    add(0, AddressBookCharModel(text = it.prefixName()))
                    charList.add(it.prefixName())
                }
            }
        }.flatten()
    }
}