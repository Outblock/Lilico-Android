package io.outblock.lilico.page.collection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.utils.viewModelIOScope

class CollectionViewModel : ViewModel() {
    val dataLiveData = MutableLiveData<List<NFTItemModel>>()

    fun load(walletAddress: String, address: String) {
        viewModelIOScope(this) {
            val data = nftListCache(walletAddress).read()?.nfts ?: return@viewModelIOScope
            dataLiveData.postValue(data.filter { it.contract.address == address }.mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) })
        }
    }

}