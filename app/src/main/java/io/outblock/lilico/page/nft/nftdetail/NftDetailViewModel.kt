package io.outblock.lilico.page.nft.nftdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.viewModelIOScope

class NftDetailViewModel : ViewModel() {

    val nftLiveData = MutableLiveData<Nft>()

    fun load(walletAddress: String, nftAddress: String, tokenId: String) {
        viewModelIOScope(this) {
            val nft = nftListCache(walletAddress).read()?.nfts?.firstOrNull { it.contract.address == nftAddress && it.id.tokenId == tokenId }
            nft?.let {
                nftLiveData.postValue(it)
                requestMeta(walletAddress, it)
            }
        }
    }

    private suspend fun requestMeta(walletAddress: String, nft: Nft) {
        val service = retrofit().create(ApiService::class.java)
        val resp = service.nftMeta(walletAddress, nft.contract.name.orEmpty(), nft.id.tokenId)
    }
}