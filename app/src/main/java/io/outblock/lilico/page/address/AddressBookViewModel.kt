package io.outblock.lilico.page.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.addressBookCache
import io.outblock.lilico.cache.recentTransactionCache
import io.outblock.lilico.manager.flowjvm.FlowJvmHelper
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.AddressBookDomain
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.address.model.AddressBookCharModel
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.send.transaction.TransactionSendActivity
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.safeRun
import io.outblock.lilico.utils.viewModelIOScope

class AddressBookViewModel : ViewModel() {
    private val addressBookList = mutableListOf<Any>()
    val addressBookLiveData = MutableLiveData<List<Any>>()

    val remoteEmptyLiveData = MutableLiveData<Boolean>()
    val localEmptyLiveData = MutableLiveData<Boolean>()
    val onSearchStartLiveData = MutableLiveData<Boolean>()

    val showProgressLiveData = MutableLiveData<Boolean>()

    val clearEditTextFocusLiveData = MutableLiveData<Boolean>()

    private var searchKeyword: String = ""

    private val isSendPage by lazy { BaseActivity.getCurrentActivity()?.javaClass == TransactionSendActivity::class.java }

    fun loadAddressBook() {
        if (searchKeyword.isNotBlank()) {
            return
        }
        viewModelIOScope(this) {
            val recentList = if (isSendPage) recentTransactionCache().read()?.contacts.orEmpty() else emptyList()

            addressBookCache().read()?.contacts?.let { updateOriginAddressBook(parseAddressBook(merge(recentList, it))) }

            val service = retrofit().create(ApiService::class.java)
            val resp = service.getAddressBook()
            resp.data.contacts?.let {
                updateOriginAddressBook(parseAddressBook(merge(recentList, it)))
                addressBookCache().cache(resp.data)
            }
            showProgressLiveData.postValue(false)
        }
    }

    fun searchKeyword() = searchKeyword

    fun searchLocal(keyword: String) {
        searchKeyword = keyword
        if (keyword.isBlank()) {
            addressBookLiveData.postValue(addressBookList)
            return
        }

        val localData = addressBookList
            .filterIsInstance<AddressBookPersonModel>()
            .map { it.data }
            .filter { it.name().contains(keyword) || it.address?.contains(keyword) ?: false }
        addressBookLiveData.postValue(parseAddressBook(localData))
        if (localData.isEmpty()) {
            localEmptyLiveData.postValue(true)
        }
    }

    fun searchRemote(keyword: String, includeLocal: Boolean = false) {
        searchKeyword = keyword
        if (keyword.isBlank()) {
            addressBookLiveData.postValue(addressBookList)
            return
        }
        onSearchStartLiveData.postValue(true)
        viewModelIOScope(this) {
            val data = mutableListOf<Any>()

            if (includeLocal) {
                val localData = addressBookList
                    .filterIsInstance<AddressBookPersonModel>()
                    .filter { it.data.name().contains(keyword) || it.data.address?.contains(keyword) ?: false }
                data.addAll(localData)
                addressBookLiveData.postValue(data)
            }

            searchUsers(keyword, data)

            if (!keyword.contains(" ") && keyword.contains(".")) {
                searchFindXyz(keyword, data)
                searchFlowns(keyword, data)
            }

            if (data.isEmpty()) {
                remoteEmptyLiveData.postValue(true)
            }
        }
    }

    fun clearSearch() {
        loadAddressBook()
    }

    fun delete(contact: AddressBookContact) {
        showProgressLiveData.postValue(true)
        viewModelIOScope(this) {
            val service = retrofit().create(ApiService::class.java)
            val resp = service.deleteAddressBook(contact.id.orEmpty())
            loadAddressBook()
            showProgressLiveData.postValue(false)
            val data = addressBookLiveData.value?.toMutableList() ?: return@viewModelIOScope
            data.removeIf { it is AddressBookPersonModel && it.data == contact }
            addressBookLiveData.postValue(data)
        }
    }

    fun isAddressBookContains(contact: AddressBookPersonModel): Boolean {
        return addressBookList.contains(contact)
    }

    fun addFriend(data: AddressBookContact) {
        showProgressLiveData.postValue(true)
        viewModelIOScope(this) {
            val service = retrofit().create(ApiService::class.java)
            try {
                val resp = service.addAddressBook(
                    mapOf(
                        "contact_name" to data.contactName,
                        "address" to data.address,
                        "domain" to data.domain?.value,
                        "domain_type" to data.domain?.domainType,
                        "username" to data.username,
                    )
                )

                if (resp.status == 200) {
                    addressBookList.add(AddressBookPersonModel(data = data))

                    val list = addressBookLiveData.value ?: emptyList()
                    list.filterIsInstance<AddressBookPersonModel>().firstOrNull { it.data == data }?.isFriend = true
                    addressBookLiveData.postValue(list)
                }
            } catch (e: Exception) {
                loge(e)
            }

            showProgressLiveData.postValue(false)
        }
    }

    fun clearInputFocus() = clearEditTextFocusLiveData.postValue(true)

    private fun updateOriginAddressBook(data: List<Any>) {
        addressBookList.clear()
        addressBookList.addAll(data)
        addressBookLiveData.postValue(data)
    }

    private suspend fun searchUsers(keyword: String, data: MutableList<Any>) {
        try {
            val service = retrofit().create(ApiService::class.java)
            val resp = service.searchUser(keyword)
            resp.data.users?.let { users ->
                if (users.isEmpty()) return@let
                data.add(AddressBookCharModel(text = "Lilico user"))
                data.addAll(users.map { AddressBookPersonModel(data = it.toContact()) })
            }
            addressBookLiveData.postValue(data)
        } catch (e: Exception) {
            loge(e)
        }
    }

    private fun searchFlowns(keyword: String, data: MutableList<Any>) {
        if (!keyword.endsWith(".fns")) {
            return
        }
        safeRun {
            val address = FlowJvmHelper().getFlownsAddress(keyword) ?: return@safeRun
            data.add(AddressBookCharModel(text = ".flowns"))
            data.add(
                AddressBookPersonModel(
                    data = AddressBookContact(
                        address = address,
                        contactName = keyword,
                        domain = AddressBookDomain(domainType = AddressBookDomain.DOMAIN_FLOWNS)
                    )
                )
            )
            addressBookLiveData.postValue(data)
        }
    }

    private fun searchFindXyz(keyword: String, data: MutableList<Any>) {
        if (!keyword.endsWith(".find")) {
            return
        }
        safeRun {
            val address = FlowJvmHelper().getFindAddress(keyword.lowercase().removeSuffix(".find")) ?: return@safeRun
            data.add(AddressBookCharModel(text = ".find"))
            data.add(
                AddressBookPersonModel(
                    data = AddressBookContact(
                        address = address,
                        contactName = keyword,
                        domain = AddressBookDomain(domainType = AddressBookDomain.DOMAIN_FIND_XYZ)
                    )
                )
            )
            addressBookLiveData.postValue(data)
        }
    }

    private fun merge(recentList: List<AddressBookContact>, addressBookList: List<AddressBookContact>): List<AddressBookContact> {
        val recent = recentList.toMutableList()
        recent.removeAll { addressBookList.firstOrNull { it.address == it.address } != null }
        return mutableListOf<AddressBookContact>().apply {
            addAll(recent)
            addAll(addressBookList)
        }
    }

    private fun parseAddressBook(data: List<AddressBookContact>): List<Any> {
        val charList = mutableListOf<String>()
        return data.sortedBy { it.name() }.map {
            mutableListOf<Any>(
                AddressBookPersonModel(it)
            ).apply {
                if (!charList.contains(it.prefixName())) {
                    add(0, AddressBookCharModel(text = "#${it.prefixName()}"))
                    charList.add(it.prefixName())
                }
            }
        }.flatten()
    }
}