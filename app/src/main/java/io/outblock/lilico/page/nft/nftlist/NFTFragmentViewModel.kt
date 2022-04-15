package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.nft.NftSelectionManager
import io.outblock.lilico.manager.nft.OnNftSelectionChangeListener
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.nftlist.model.CollectionItemModel
import io.outblock.lilico.page.nft.nftlist.model.CollectionTabsModel
import io.outblock.lilico.page.nft.nftlist.model.CollectionTitleModel
import io.outblock.lilico.page.nft.nftlist.model.NFTItemModel
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
        loadSelectionCards()
        loadListDataFromCache()
        loadListDataFromServer()
    }

    private suspend fun loadListDataFromServer() {
        val data = mutableListOf<Any>()
        val service = retrofit().create(ApiService::class.java)
        val resp = service.nftList(address!!, 0, 100)

        if (resp.data == null) {
            cacheNftList.clear()
        } else {
            cacheNftList.cache(resp.data)
        }

        resp.data?.nfts?.parseToCollectionList()?.let { collections -> data.addCollections(collections) }

        emptyLiveData.postValue(data.isEmpty())

        listDataLiveData.postValue(data)
    }

    private fun loadListDataFromCache() {
        val data = mutableListOf<Any>()
        cacheNftList.read()?.nfts?.parseToCollectionList()?.let { collections -> data.addCollections(collections) }

        listDataLiveData.postValue(data)
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
        cacheNftList.read()?.nfts?.toMutableList()?.let { nftList ->
            gridDataLiveData.postValue(nftList.removeEmpty().mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) })
        }
        val service = retrofit().create(ApiService::class.java)
        val resp = service.nftList(address!!, 0, 100)
        if (resp.data == null) {
            cacheNftList.clear()
        } else {
            cacheNftList.cache(resp.data)
        }
        emptyLiveData.postValue(resp.data?.nfts?.isNullOrEmpty() ?: true)
        gridDataLiveData.postValue(
            resp.data?.nfts?.toMutableList()?.removeEmpty()?.mapIndexed { index, nft -> NFTItemModel(nft = nft, index = index) }.orEmpty()
        )
    }

    private fun getNftAddress(): String? {
        //0x53f389d96fb4ce5e
        //0x2b06c41f44a05656
        //0xccea80173b51e028
        //0x4ab2b65a8b2be2aa
        if (BuildConfig.DEBUG) {
            return "0x2b06c41f44a05656"
        }
        return cacheWallet.read()?.primaryWalletAddress()
    }

    private fun loadSelectionCards() {
        val data = cacheSelections.read() ?: NftSelections(mutableListOf())
        topSelectionLiveData.postValue(data)
    }

    companion object {
        private val TAG = NFTFragmentViewModel::class.java.simpleName
    }
}