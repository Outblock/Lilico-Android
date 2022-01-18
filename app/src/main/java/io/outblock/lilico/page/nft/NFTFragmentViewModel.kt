package io.outblock.lilico.page.nft

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.R
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftListCache
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.nft.model.HeaderPlaceholderModel
import io.outblock.lilico.page.nft.model.NFTItemModel
import io.outblock.lilico.page.nft.model.NFTTitleModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.logw
import io.outblock.lilico.utils.viewModelIOScope

class NFTFragmentViewModel : ViewModel() {

    val dataLiveData = MutableLiveData<List<Any>>()
    val selectionIndexLiveData = MutableLiveData<Int>()

    private var isGridMode = false

    private val address by lazy { getNftAddress() }
    private val cacheNftList by lazy { nftListCache(address) }
    private val cacheSelections by lazy { nftSelectionCache() }
    private val cacheWallet by lazy { walletCache() }

    fun load() {
        viewModelIOScope(this) {
            if (address.isNullOrEmpty()) {
                logw(TAG, "address not found")
                return@viewModelIOScope
            }
            if (isGridMode) loadGridData() else loadListData()
        }
    }

    fun updateLayoutMode(isGridMode: Boolean) {
        this.isGridMode = isGridMode
        load()
    }

    fun updateSelectionIndex(position: Int) {
        selectionIndexLiveData.postValue(position)
    }

    fun isGridMode() = isGridMode

    fun getWalletAddress() = address

    private suspend fun loadListData() {
        val data = mutableListOf<Any>()
        val selections = cacheSelections.read() ?: NftSelections(emptyList())
        if (selections.data.isNotEmpty()) {
            data.add(selections)
        }
        if (!isGridMode) {
            dataLiveData.postValue(data.addHeader())
        }
    }

    private suspend fun loadGridData() {
        cacheNftList.read()?.nfts?.let { nftList ->
            dataLiveData.postValue(nftList.map { NFTItemModel(walletAddress = address!!, nft = it) }.addHeader())
        }
        val service = retrofit().create(ApiService::class.java)
        val resp = service.nftList(address!!, 0, 100)
        cacheNftList.cache(resp.data)
        if (isGridMode) {
            dataLiveData.postValue(resp.data.nfts.map { NFTItemModel(walletAddress = address!!, nft = it) }.addHeader())
        }
    }

    private fun getNftAddress(): String? {
        //0x53f389d96fb4ce5e
        //0x2b06c41f44a05656
        //0xccea80173b51e028
        if (BuildConfig.DEBUG) {
            return "0x2b06c41f44a05656"
        }
        return cacheWallet.read()?.primaryWallet()?.blockchain?.firstOrNull()?.address
    }

    private fun List<Any>.addHeader(): List<Any> {
        if (size == 0) {
            return this
        }
        val list = this
        return mutableListOf(
            HeaderPlaceholderModel(),
            NFTTitleModel(
                icon = R.drawable.ic_collection_star,
                iconTint = if (isGridMode) R.color.text.res2color() else R.color.white.res2color(),
                text = R.string.top_selection.res2String(),
                textColor = if (isGridMode) R.color.text.res2color() else R.color.white.res2color()
            ),
        ).apply { addAll(list) }
    }

    companion object {
        private val TAG = NFTFragmentViewModel::class.java.simpleName
    }
}