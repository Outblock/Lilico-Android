package io.outblock.lilico.page.nftdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.viewModelIOScope

class NftDetailViewModel : ViewModel() {

    val nftLiveData = MutableLiveData<Nft>()

    fun load(walletAddress: String, nftAddress: String) {
        viewModelIOScope(this) {
            val nft = nftListCache(walletAddress).read()?.nfts?.firstOrNull { it.contract.address == nftAddress }
            nft?.let { nftLiveData.postValue(it) }
        }
    }
}