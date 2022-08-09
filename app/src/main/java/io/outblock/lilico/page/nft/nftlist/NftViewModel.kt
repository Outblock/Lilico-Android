package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.NftCollectionWrapper
import io.outblock.lilico.page.nft.nftlist.model.CollectionItemModel
import io.outblock.lilico.page.nft.nftlist.model.CollectionTitleModel
import io.outblock.lilico.page.nft.nftlist.model.NFTCountTitleModel
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
import io.outblock.lilico.page.nft.nftlist.utils.NftGridRequester
import io.outblock.lilico.page.nft.nftlist.utils.NftList
import io.outblock.lilico.page.nft.nftlist.utils.NftListRequester
import io.outblock.lilico.utils.*

class NftViewModel : ViewModel() {

    val collectionsLiveData = MutableLiveData<List<CollectionItemModel>>()
    val collectionTitleLiveData = MutableLiveData<CollectionTitleModel>()
    val listNftLiveData = MutableLiveData<List<NFTItemModel>>()
    val collectionTabChangeLiveData = MutableLiveData<String>()

    val gridNftLiveData = MutableLiveData<List<Any>>()

    val emptyLiveData = MutableLiveData<Boolean>()

    private val gridRequester by lazy { NftGridRequester() }
    private val listRequester by lazy { NftListRequester() }

    private var selectedCollection: NftCollection? = null
    private var isCollectionExpanded = false

    fun requestList() {
        viewModelIOScope(this) {
            isCollectionExpanded = isNftCollectionExpanded()
            logd("xxx", "isCollectionExpanded:$isCollectionExpanded")

            // read from cache
            val cacheCollections = listRequester.cacheCollections().orEmpty()
            notifyCollectionList(cacheCollections)
            collectionTitleLiveData.postValue(CollectionTitleModel(count = cacheCollections.size))
            (selectedCollection ?: cacheCollections.firstOrNull()?.collection)?.let { notifyNftList(listRequester.cachedNfts(it)) }

            // read from server
            val onlineCollections = listRequester.requestCollection().orEmpty()

            emptyLiveData.postValue(onlineCollections.isEmpty())

            collectionTitleLiveData.postValue(CollectionTitleModel(count = onlineCollections.size))
            notifyCollectionList(onlineCollections)
            (selectedCollection ?: onlineCollections.firstOrNull()?.collection)?.let { notifyNftList(listRequester.request(it)) }
        }
    }

    fun requestGrid() {
        viewModelIOScope(this) {
            notifyGridList(gridRequester.cachedNfts())
            val nftList = gridRequester.request()
            notifyGridList(nftList)
            emptyLiveData.postValue(nftList.count == 0)
        }
    }

    fun toggleCollectionExpand() {
        ioScope {
            updateNftCollectionExpanded(!isCollectionExpanded)
            isCollectionExpanded = isNftCollectionExpanded()
            requestList()
        }
    }

    fun selectCollection(collectionAddress: String) {
        if (selectedCollection?.address() == collectionAddress) {
            return
        }
        val collection = listRequester.cacheCollections()?.firstOrNull { it.collection?.address() == collectionAddress } ?: return
        collectionTabChangeLiveData.postValue(collectionAddress)
        selectedCollection = collection.collection
        viewModelIOScope(this) {
            val tmpCollection = selectedCollection ?: return@viewModelIOScope
            notifyNftList(listRequester.cachedNfts(tmpCollection))

            val nfts = listRequester.request(tmpCollection)
            if (tmpCollection.contractName == selectedCollection?.contractName) {
                notifyNftList(nfts)
            }
        }
    }

    fun isCollectionExpanded() = isCollectionExpanded

    private fun notifyNftList(nfts: List<Nft>?) {
        listNftLiveData.postValue(nfts.orEmpty().map { NFTItemModel(nft = it) })
    }

    private fun notifyCollectionList(collections: List<NftCollectionWrapper>?) {
        val selectedAddress = selectedCollection?.address() ?: collections?.firstOrNull()?.collection?.address()
        collectionsLiveData.postValue(collections.orEmpty().map {
            val collection = it.collection
            CollectionItemModel(
                name = collection?.name,
                count = it.count ?: 0,
                address = collection?.address().orEmpty(),
                nfts = listOf(),
                isSelected = selectedAddress == collection?.address(),
            )
        })
    }

    private fun notifyGridList(nftList: NftList) {
        if (nftList.count == 0) {
            return
        }

        val list = mutableListOf<Any>()

        if (gridNftLiveData.value.isNullOrEmpty()) {
            list.add(NFTCountTitleModel(nftList.count))
        }
        list.addAll(gridRequester.dataList().map { NFTItemModel(nft = it) })
        gridNftLiveData.postValue(list)
    }
}