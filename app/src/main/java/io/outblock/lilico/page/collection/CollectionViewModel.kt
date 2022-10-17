package io.outblock.lilico.page.collection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.model.NftCollectionWrapper
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.page.nft.nftlist.model.NftLoadMoreModel
import io.outblock.lilico.page.nft.nftlist.nftWalletAddress
import io.outblock.lilico.page.nft.nftlist.utils.NftCache
import io.outblock.lilico.page.nft.nftlist.utils.NftListRequester
import io.outblock.lilico.utils.viewModelIOScope

class CollectionViewModel : ViewModel() {
    val dataLiveData = MutableLiveData<List<Any>>()
    val collectionLiveData = MutableLiveData<NftCollectionWrapper>()

    private var collectionWrapper: NftCollectionWrapper? = null

    private val nftCache by lazy { NftCache(nftWalletAddress()) }

    private val requester by lazy { NftListRequester() }

    fun load(contractName: String) {
        viewModelIOScope(this) {
            val collectionWrapper = nftCache.collection()
                .read()?.collections
                ?.firstOrNull { it.collectionOrigin?.contractName == contractName } ?: return@viewModelIOScope

            this.collectionWrapper = collectionWrapper

            val collection = collectionWrapper.collectionOrigin ?: return@viewModelIOScope

            collectionLiveData.postValue(collectionWrapper)
            notifyNftList()

            requester.request(collection)
            notifyNftList()
        }
    }

    fun requestListNextPage() {
        viewModelIOScope(this) {
            val collection = collectionWrapper?.collectionOrigin ?: return@viewModelIOScope
            requester.nextPage(collection)
            notifyNftList()
        }
    }

    private fun notifyNftList() {
        val collection = collectionWrapper?.collectionOrigin ?: return
        val list = mutableListOf<Any>().apply { addAll(requester.dataList(collection).map { NFTItemModel(nft = it) }) }
        if (requester.haveMore()) {
            list.add(NftLoadMoreModel(isListLoadMore = true))
        }
        dataLiveData.postValue(list)
    }
}