package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.OnWalletDataUpdate
import io.outblock.lilico.manager.account.WalletManager
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.NftCollectionWrapper
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.nft.nftlist.model.*
import io.outblock.lilico.page.nft.nftlist.utils.*
import io.outblock.lilico.utils.*

private val TAG = NftViewModel::class.java.simpleName

class NftViewModel : ViewModel(), OnNftFavoriteChangeListener, OnWalletDataUpdate {

    val collectionsLiveData = MutableLiveData<List<CollectionItemModel>>()
    val collectionTitleLiveData = MutableLiveData<CollectionTitleModel>()
    val listNftLiveData = MutableLiveData<List<Any>>()
    val collectionTabChangeLiveData = MutableLiveData<String>()
    val favoriteLiveData = MutableLiveData<List<Nft>>()
    val favoriteIndexLiveData = MutableLiveData<Int>()

    val gridNftLiveData = MutableLiveData<List<Any>>()

    val emptyLiveData = MutableLiveData<Boolean>()
    val listScrollChangeLiveData = MutableLiveData<Int>()

    private val gridRequester by lazy { NftGridRequester() }
    private val listRequester by lazy { NftListRequester() }

    private var selectedCollection: NftCollection? = null
    private var isCollectionExpanded = false

    init {
        NftFavoriteManager.addOnNftSelectionChangeListener(this)
        observeWalletUpdate()
    }

    fun requestList() {
        viewModelIOScope(this) {
            isCollectionExpanded = isNftCollectionExpanded()

            // read from cache
            val cacheCollections = listRequester.cacheCollections().orEmpty()
            notifyCollectionList(cacheCollections)
            updateDefaultSelectCollection()

            collectionTitleLiveData.postValue(CollectionTitleModel(count = cacheCollections.size))
            (selectedCollection ?: cacheCollections.firstOrNull()?.collectionOrigin)?.let {
                logd(TAG, "notifyNftList 2")
                notifyNftList(it)
            }

            if (cacheCollections.isNotEmpty()) {
                emptyLiveData.postValue(false)
            }

            requestFavorite()

            // read from server
            val onlineCollections = listRequester.requestCollection().orEmpty()

            emptyLiveData.postValue(onlineCollections.isEmpty())

            collectionTitleLiveData.postValue(CollectionTitleModel(count = onlineCollections.size))
            notifyCollectionList(onlineCollections)
            updateDefaultSelectCollection()

            (selectedCollection ?: onlineCollections.firstOrNull()?.collectionOrigin)?.let {
                listRequester.request(it)
                logd(TAG, "notifyNftList 1")
                notifyNftList(it)
            }
        }
    }

    override fun onNftFavoriteChange(nfts: List<Nft>) {
        favoriteLiveData.postValue(nfts)
    }

    private fun requestFavorite() {
        viewModelIOScope(this) { NftFavoriteManager.request() }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        requestList()
        requestGrid()
    }

    private fun observeWalletUpdate() {
        ioScope {
            // wallet not loaded yet
            if (nftWalletAddress().isEmpty()) {
                logd(TAG, "wallet not loaded yet")
                WalletManager.addListener(this)
            }
        }
    }

    private fun updateDefaultSelectCollection() {
        val cacheCollections = listRequester.cacheCollections()
        selectedCollection = selectedCollection ?: cacheCollections?.firstOrNull()?.collectionOrigin
        if (selectedCollection != null && cacheCollections?.firstOrNull { it.collectionOrigin?.contractName == selectedCollection?.contractName } == null) {
            cacheCollections?.firstOrNull()?.collectionOrigin?.contractName?.let { selectCollection(it) }
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
            logd(TAG, "notifyNftList 3")
            notifyNftList(collection)
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

    fun updateSelectionIndex(position: Int) {
        favoriteIndexLiveData.value = position
    }

    fun selectCollection(contractName: String) {
        if (selectedCollection?.contractName == contractName) {
            return
        }
        val collection = listRequester.cacheCollections()?.firstOrNull { it.collectionOrigin?.contractName == contractName } ?: return
        collectionTabChangeLiveData.postValue(contractName)
        selectedCollection = collection.collectionOrigin
        viewModelIOScope(this) {
            val tmpCollection = selectedCollection ?: return@viewModelIOScope
            logd(TAG, "notifyNftList 4")
            notifyNftList(tmpCollection)

            listRequester.request(tmpCollection)
            logd(TAG, "notifyNftList 5")
            notifyNftList(tmpCollection)
        }
    }

    fun isCollectionExpanded() = isCollectionExpanded

    fun onListScrollChange(scrollY: Int) = apply { listScrollChangeLiveData.postValue(scrollY) }

    private fun notifyNftList(collection: NftCollection) {
        if (collection.contractName != selectedCollection?.contractName) {
            return
        }
        val list = mutableListOf<Any>().apply { addAll(listRequester.dataList(collection).map { NFTItemModel(nft = it) }) }
        if (list.isNotEmpty() && listRequester.haveMore()) {
            list.add(NftLoadMoreModel(isListLoadMore = true))
        }

        logd(TAG, "notifyNftList collection:${collection.name} size:${list.size}")

        if (list.isEmpty()) {
            val count = listRequester.cacheCollections()?.firstOrNull { it.collectionOrigin?.contractName == collection.contractName }?.count ?: 0
            list.addAll(generateEmptyNftPlaceholders(count))
        }

        listNftLiveData.postValue(list)
    }

    private fun notifyCollectionList(collections: List<NftCollectionWrapper>?) {
        val selectedCollection = selectedCollection?.contractName ?: collections?.firstOrNull()?.collectionOrigin?.contractName
        collectionsLiveData.postValue(collections.orEmpty().mapNotNull {
            val collection = it.collectionOrigin ?: return@mapNotNull null
            CollectionItemModel(
                collection = collection,
                count = it.count ?: 0,
                isSelected = selectedCollection == collection.contractName
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