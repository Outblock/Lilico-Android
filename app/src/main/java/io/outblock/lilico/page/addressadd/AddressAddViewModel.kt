package io.outblock.lilico.page.addressadd

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.flowjvm.FlowJvmHelper
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.viewModelIOScope

class AddressAddViewModel : ViewModel() {
    val resultLiveData = MutableLiveData<Boolean>()

    val addressVerifyStateLiveData = MutableLiveData<Int>()

    private var address: String = ""

    private var contact: AddressBookContact? = null

    fun setContact(contact: AddressBookContact?) {
        this.contact = contact
    }

    fun save(name: String, address: String) {
        viewModelIOScope(this) {
            if (contact == null) createContact(name, address) else editContact(name, address)
        }
    }

    private suspend fun editContact(name: String, address: String) {
        val contact = contact ?: return
        val service = retrofit().create(ApiService::class.java)
        try {
            val resp = service.addAddressBook(
                mapOf(
                    "id" to contact.id.orEmpty(),
                    "contact_name" to name,
                    "address" to address,
                    "domain" to contact.domain?.value.orEmpty(),
                    "domain_type" to (contact.domain?.domainType ?: 0),
                )
            )
            resultLiveData.postValue(resp.status == 200)
        } catch (e: Exception) {
            loge(e)
            resultLiveData.postValue(false)
        }
    }

    private suspend fun createContact(name: String, address: String) {
        val service = retrofit().create(ApiService::class.java)
        try {
            val resp = service.addAddressBook(mapOf("contact_name" to name, "address" to address, "domain" to "", "domain_type" to 0))
            resultLiveData.postValue(resp.status == 200)
        } catch (e: Exception) {
            loge(e)
            resultLiveData.postValue(false)
        }
    }

    fun addressVerify(address: String) {
        this.address = address
        if (address.length != 18) {
            addressVerifyStateLiveData.postValue(ADDRESS_VERIFY_STATE_LENGTH_ERROR)
            return
        } else {
            addressVerifyStateLiveData.postValue(ADDRESS_VERIFY_STATE_PENDING)
        }
        viewModelIOScope(this) {
            val isVerified = FlowJvmHelper().addressVerify(address)
            if (address == this.address) {
                addressVerifyStateLiveData.postValue(if (isVerified) ADDRESS_VERIFY_STATE_SUCCESS else ADDRESS_VERIFY_STATE_ERROR)
            }
        }
    }
}