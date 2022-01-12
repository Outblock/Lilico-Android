package io.outblock.lilico.page.nft

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.viewModelIOScope

class NFTFragmentViewModel : ViewModel() {

    val dataLiveData = MutableLiveData<List<Any>>()

    fun load() {
        viewModelIOScope(this) {
            val service = retrofit().create(ApiService::class.java)
            val resp = service.nftList()
            dataLiveData.postValue(resp.data.nfts)
        }
    }
}