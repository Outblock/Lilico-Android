package io.outblock.lilico.page.collection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.page.nft.nftlist.nftWalletAddress
import io.outblock.lilico.page.nft.nftlist.utils.NftCache
import io.outblock.lilico.utils.viewModelIOScope

class CollectionViewModel : ViewModel() {
    val dataLiveData = MutableLiveData<List<NFTItemModel>>()

    fun load(address: String) {
        viewModelIOScope(this) {
            val data = NftCache(nftWalletAddress()).list(address).read()?.list ?: return@viewModelIOScope
            dataLiveData.postValue(data.filter { it.contract.address == address }.mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) })
        }
    }

}