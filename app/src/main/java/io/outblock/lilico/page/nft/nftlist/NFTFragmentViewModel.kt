package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.account.OnWalletDataUpdate
import io.outblock.lilico.manager.account.WalletManager
import io.outblock.lilico.manager.nft.NftSelectionManager
import io.outblock.lilico.manager.nft.OnNftSelectionChangeListener
import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.nft.nftlist.model.*
import io.outblock.lilico.utils.*

class NFTFragmentViewModel : ViewModel(), OnNftSelectionChangeListener, OnWalletDataUpdate {

    val listDataLiveData = MutableLiveData<List<Any>>()
    val topSelectionLiveData = MutableLiveData<NftSelections>()
    val selectionIndexLiveData = MutableLiveData<Int>()
    val listScrollChangeLiveData = MutableLiveData<Int>()
    val collectionTabChangeLiveData = MutableLiveData<String>()
    val collectionTabsLiveData = MutableLiveData<CollectionTabsModel>()
    val collectionTitleLiveData = MutableLiveData<CollectionTitleModel>()
    val collectionExpandChangeLiveData = MutableLiveData<Boolean>()
    val emptyLiveData = MutableLiveData<Boolean>()

    val gridDataLiveData = MutableLiveData<List<Any>>()

    private var isGridMode = false
    private var isCollectionExpanded = false
    private var selectedCollection = ""

    private val cacheWallet by lazy { walletCache() }

    init {
        NftSelectionManager.addOnNftSelectionChangeListener(this)
        observeWalletUpdate()
    }

    fun refresh() {
        loadList()
        loadGrid()
    }

    fun updateLayoutMode(isGridMode: Boolean) {
        this.isGridMode = isGridMode
    }

    fun isCollectionExpanded() = isCollectionExpanded

    fun updateSelectionIndex(position: Int) {
        selectionIndexLiveData.value = position
    }

    fun isGridMode() = isGridMode

    fun getWalletAddress() = address()

    fun toggleCollectionExpand() {
        ioScope {
            updateNftCollectionExpanded(!isCollectionExpanded)
            isCollectionExpanded = isNftCollectionExpanded()
            collectionExpandChangeLiveData.postValue(isCollectionExpanded)
            loadListDataFromCache()
        }
    }

    fun updateSelectCollections(address: String) {
        selectedCollection = address
        collectionTabChangeLiveData.postValue(address)
        loadListDataFromCache()
    }

    fun onListScrollChange(scrollY: Int) {
        listScrollChangeLiveData.postValue(scrollY)
    }

    override fun onAddSelection(nft: Nft) {
        viewModelIOScope(this) {
            loadSelectionCards()
        }
    }

    override fun onRemoveSelection(nft: Nft) {
        viewModelIOScope(this) {
            loadSelectionCards()
        }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        refresh()
    }

    private fun loadList() {
        viewModelIOScope(this) {
            if (address().isNullOrEmpty()) {
                logw(TAG, "address not found")
                emptyLiveData.postValue(true)
                return@viewModelIOScope
            }
            isCollectionExpanded = isNftCollectionExpanded()
            collectionExpandChangeLiveData.postValue(isCollectionExpanded)
            loadListData()
        }
    }

    private fun loadGrid() {
        viewModelIOScope(this) {
            if (address().isNullOrEmpty()) {
                logw(TAG, "address not found")
                return@viewModelIOScope
            }
            loadGridData()
        }
    }

    private suspend fun loadListData() {
        var nftList = loadListDataFromCache()
        loadSelectionCards(nftList)
        nftList = loadListDataFromServer()
        // remove selection if not contain in nft list
        loadSelectionCards(nftList)
    }

    private suspend fun loadListDataFromServer(): NFTListData? {
        val data = mutableListOf<Any>()
        val resp = requestNftListFromServer(address()!!)

        if (resp == null) {
            cacheNftList().clear()
        } else {
            cacheNftList().cache(resp)
        }

        resp?.nfts?.parseToCollectionList()?.let { collections -> data.addCollections(collections) }

        emptyLiveData.postValue(data.isEmpty())

        listDataLiveData.postValue(data)
        return resp
    }

    private fun loadListDataFromCache(): NFTListData? {
        logd(TAG, "loadListDataFromCache start")
        val data = mutableListOf<Any>()
        val nftListData = cacheNftList().read()
        nftListData?.nfts?.parseToCollectionList()?.let { collections -> data.addCollections(collections) }

        if (data.isNotEmpty()) {
            emptyLiveData.postValue(false)
        }

        listDataLiveData.postValue(data)
        logd(TAG, "loadListDataFromCache: size=${data.size}")
        return nftListData
    }

    private fun MutableList<Any>.addCollections(collections: List<CollectionItemModel>) {
        if (collections.isNotEmpty()) {
            collectionTitleLiveData.postValue(CollectionTitleModel(count = collections.size))
            if (selectedCollection.isEmpty()) {
                selectedCollection = collections.first().address
            }
            collections.forEach { it.isSelected = it.address == selectedCollection }
            collectionTabsLiveData.postValue(CollectionTabsModel(collections = collections))
            if (isCollectionExpanded) {
                addAll(collections
                    .first { it.address == selectedCollection }
                    .nfts?.mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) }
                    .orEmpty())
            } else {
                addAll(collections)
            }
        }
    }

    private suspend fun loadGridData() {
        cacheNftList().read()?.nfts?.toMutableList()?.let { nftList -> notifyGridList(nftList) }
        val resp = requestNftListFromServer(address()!!)
        if (resp == null) {
            cacheNftList().clear()
        } else {
            cacheNftList().cache(resp)
        }
        emptyLiveData.postValue(resp?.nfts.isNullOrEmpty())
        notifyGridList(resp?.nfts)
    }

    private fun loadSelectionCards(nftList: NFTListData? = null) {
        val nfts = (nftList?.nfts ?: cacheNftList().read()?.nfts) ?: return
        val data = cacheSelections().read() ?: NftSelections(mutableListOf())
        data.data = data.data.filter { selection -> nfts.firstOrNull { nft -> selection.uniqueId() == nft.uniqueId() } != null }.toMutableList()
        topSelectionLiveData.postValue(data)
    }

    private fun notifyGridList(nftList: List<Nft>?) {
        val nftItems = nftList?.toMutableList()?.removeEmpty()?.mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) }
        if (nftItems.isNullOrEmpty()) {
            gridDataLiveData.postValue(emptyList())
            return
        }
        val list = mutableListOf<Any>(
            NFTCountTitleModel(nftItems.size)
        )
        list.addAll(nftItems)
        logd(TAG, "notifyGridList: size=${nftItems.size}")
        gridDataLiveData.postValue(list)
    }

    private fun observeWalletUpdate() {
        ioScope {
            // wallet not loaded yet
            if (address().isNullOrEmpty()) {
                logd(TAG, "wallet not loaded yet")
                WalletManager.addListener(this)
            }
        }
    }

    private fun address(): String? {
        return cacheWallet.read()?.primaryWalletAddress()
    }

    private fun cacheNftList() = nftListCache(address())

    private fun cacheSelections() = nftSelectionCache(address())

    companion object {
        private val TAG = NFTFragmentViewModel::class.java.simpleName
    }
}