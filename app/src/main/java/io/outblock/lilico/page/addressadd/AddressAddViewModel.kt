package io.outblock.lilico.page.addressadd

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.viewModelIOScope

class AddressAddViewModel : ViewModel() {
    val resultLiveData = MutableLiveData<Boolean>()

    fun save(name: String, address: String) {
        viewModelIOScope(this) {
            val service = retrofit().create(ApiService::class.java)
            try {
                val resp = service.addAddressBook(mapOf("contact_name" to name, "address" to address, "domain" to "", "domain_type" to 0))
                resultLiveData.postValue(resp.status == 200)
            } catch (e: Exception) {
                loge(e)
                resultLiveData.postValue(false)
            }
        }
    }
}