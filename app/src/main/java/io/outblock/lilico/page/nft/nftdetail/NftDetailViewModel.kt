package io.outblock.lilico.page.nft.nftdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.nftWalletAddress
import io.outblock.lilico.page.nft.nftlist.utils.NftCache
import io.outblock.lilico.utils.viewModelIOScope

class NftDetailViewModel : ViewModel() {

    val nftLiveData = MutableLiveData<Nft>()

    fun load(uniqueId: String) {
        viewModelIOScope(this) {
            val walletAddress = nftWalletAddress()
            val nft = NftCache(walletAddress).findNftById(uniqueId)
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