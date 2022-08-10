package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.network.model.NftCollectionWrapper
import io.outblock.lilico.page.nft.nftlist.model.*
import io.outblock.lilico.page.nft.nftlist.utils.NftFavoriteRequester
import io.outblock.lilico.page.nft.nftlist.utils.NftGridRequester
import io.outblock.lilico.page.nft.nftlist.utils.NftList
import io.outblock.lilico.page.nft.nftlist.utils.NftListRequester
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isNftCollectionExpanded
import io.outblock.lilico.utils.updateNftCollectionExpanded
import io.outblock.lilico.utils.viewModelIOScope

class NftViewModel : ViewModel() {

    val collectionsLiveData = MutableLiveData<List<CollectionItemModel>>()
    val collectionTitleLiveData = MutableLiveData<CollectionTitleModel>()
    val listNftLiveData = MutableLiveData<List<Any>>()
    val collectionTabChangeLiveData = MutableLiveData<String>()

    val gridNftLiveData = MutableLiveData<List<Any>>()

    val emptyLiveData = MutableLiveData<Boolean>()
    val listScrollChangeLiveData = MutableLiveData<Int>()

    private val gridRequester by lazy { NftGridRequester() }
    private val listRequester by lazy { NftListRequester() }
    private val favoriteRequester by lazy { NftFavoriteRequester() }

    private var selectedCollection: NftCollection? = null
    private var isCollectionExpanded = false

    fun requestList() {
        viewModelIOScope(this) {
            isCollectionExpanded = isNftCollectionExpanded()

            // read from cache
            val cacheCollections = listRequester.cacheCollections().orEmpty()
            notifyCollectionList(cacheCollections)
            collectionTitleLiveData.postValue(CollectionTitleModel(count = cacheCollections.size))
            (selectedCollection ?: cacheCollections.firstOrNull()?.collection)?.let { notifyNftList(it) }

            updateDefaultSelectCollection()

            if (cacheCollections.isNotEmpty()) {
                emptyLiveData.postValue(false)
            }

            // read from server
            val onlineCollections = listRequester.requestCollection().orEmpty()

            emptyLiveData.postValue(onlineCollections.isEmpty())

            collectionTitleLiveData.postValue(CollectionTitleModel(count = onlineCollections.size))
            notifyCollectionList(onlineCollections)

            updateDefaultSelectCollection()

            (selectedCollection ?: onlineCollections.firstOrNull()?.collection)?.let {
                listRequester.request(it)
                notifyNftList(it)
            }

            favoriteRequester.request()
        }
    }

    private fun updateDefaultSelectCollection() {
        val cacheCollections = listRequester.cacheCollections()
        selectedCollection = selectedCollection ?: cacheCollections?.firstOrNull()?.collection
        if (selectedCollection != null && cacheCollections?.firstOrNull { it.collection?.contractName == selectedCollection?.contractName } == null) {
            cacheCollections?.firstOrNull()?.collection?.address()?.let { selectCollection(it) }
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

    fun requestListNextPage() {
        viewModelIOScope(this) {
            val collection = selectedCollection ?: return@viewModelIOScope
            listRequester.nextPage(collection)
            if (collection.contractName == selectedCollection?.contractName) {
                notifyNftList(collection)
            }
        }
    }

    fun requestGridNextPage() {
        viewModelIOScope(this) {
            notifyGridList(gridRequester.nextPage())
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
            notifyNftList(tmpCollection)

            listRequester.request(tmpCollection)
            if (tmpCollection.contractName == selectedCollection?.contractName) {
                notifyNftList(tmpCollection)
            }
        }
    }

    fun isCollectionExpanded() = isCollectionExpanded

    fun onListScrollChange(scrollY: Int) = apply { listScrollChangeLiveData.postValue(scrollY) }

    private fun notifyNftList(collection: NftCollection) {
        val list = mutableListOf<Any>().apply { addAll(listRequester.dataList(collection).map { NFTItemModel(nft = it) }) }
        if (listRequester.haveMore()) {
            list.add(NftLoadMoreModel(isListLoadMore = true))
        }
        listNftLiveData.postValue(list)
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

        val list = mutableListOf<Any>(NFTCountTitleModel(nftList.count))

        list.addAll(gridRequester.dataList().map { NFTItemModel(nft = it) })
        if (gridRequester.haveMore()) {
            list.add(NftLoadMoreModel(isGridLoadMore = true))
        }
        gridNftLiveData.postValue(list)
    }
}