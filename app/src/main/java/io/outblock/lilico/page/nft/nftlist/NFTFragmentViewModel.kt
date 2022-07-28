package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.nft.NftSelectionManager
import io.outblock.lilico.manager.nft.OnNftSelectionChangeListener
import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.nftlist.model.*
import io.outblock.lilico.utils.*

class NFTFragmentViewModel : ViewModel(), OnNftSelectionChangeListener {

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

    private val address by lazy { getNftAddress() }
    private val cacheNftList by lazy { nftListCache(address) }
    private val cacheSelections by lazy { nftSelectionCache() }
    private val cacheWallet by lazy { walletCache() }

    init {
        NftSelectionManager.addOnNftSelectionChangeListener(this)
    }

    fun loadList() {
        viewModelIOScope(this) {
            if (address.isNullOrEmpty()) {
                logw(TAG, "address not found")
                emptyLiveData.postValue(true)
                return@viewModelIOScope
            }
            isCollectionExpanded = isNftCollectionExpanded()
            collectionExpandChangeLiveData.postValue(isCollectionExpanded)
            loadListData()
        }
    }

    fun loadGrid() {
        viewModelIOScope(this) {
            if (address.isNullOrEmpty()) {
                logw(TAG, "address not found")
                return@viewModelIOScope
            }
            loadGridData()
        }
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

    fun getWalletAddress() = address

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

    private suspend fun loadListData() {
        var nftList = loadListDataFromCache()
        loadSelectionCards(nftList)
        nftList = loadListDataFromServer()
        // remove selection if not contain in nft list
        loadSelectionCards(nftList)
    }

    private suspend fun loadListDataFromServer(): NFTListData? {
        val data = mutableListOf<Any>()
        val resp = requestNftListFromServer(address!!)

        if (resp == null) {
            cacheNftList.clear()
        } else {
            cacheNftList.cache(resp)
        }

        resp?.nfts?.parseToCollectionList()?.let { collections -> data.addCollections(collections) }

        emptyLiveData.postValue(data.isEmpty())

        listDataLiveData.postValue(data)
        return resp
    }

    private fun loadListDataFromCache(): NFTListData? {
        val data = mutableListOf<Any>()
        val nftListData = cacheNftList.read()
        nftListData?.nfts?.parseToCollectionList()?.let { collections -> data.addCollections(collections) }
        listDataLiveData.postValue(data)
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
        cacheNftList.read()?.nfts?.toMutableList()?.let { nftList -> notifyGridList(nftList) }
        val resp = requestNftListFromServer(address!!)
        if (resp == null) {
            cacheNftList.clear()
        } else {
            cacheNftList.cache(resp)
        }
        emptyLiveData.postValue(resp?.nfts.isNullOrEmpty())
        notifyGridList(resp?.nfts)
    }

    private fun getNftAddress(): String? {
        return cacheWallet.read()?.primaryWalletAddress()
    }

    private fun loadSelectionCards(nftList: NFTListData? = null) {
        val nfts = (nftList?.nfts ?: cacheNftList.read()?.nfts) ?: return
        val data = cacheSelections.read() ?: NftSelections(mutableListOf())
        data.data = data.data.filter { selection -> nfts.firstOrNull { nft -> selection.uniqueId() == nft.uniqueId() } != null }.toMutableList()
        topSelectionLiveData.postValue(data)
    }

    private fun notifyGridList(nftList: List<Nft>?) {
        if (nftList.isNullOrEmpty()) {
            gridDataLiveData.postValue(emptyList())
            return
        }
        val list = mutableListOf<Any>(
            NFTCountTitleModel(nftList.size)
        )
        list.addAll(nftList.toMutableList().removeEmpty().mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) })
        gridDataLiveData.postValue(list)
    }

    companion object {
        private val TAG = NFTFragmentViewModel::class.java.simpleName
    }
}