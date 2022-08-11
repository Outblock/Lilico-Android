package io.outblock.lilico.page.nft.nftlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.BuildConfig
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
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope

class NFTFragmentViewModelV0 : ViewModel(), OnNftSelectionChangeListener, OnWalletDataUpdate {

    val topSelectionLiveData = MutableLiveData<NftSelections>()
    val selectionIndexLiveData = MutableLiveData<Int>()

    private val cacheWallet by lazy { walletCache() }

    init {
        NftSelectionManager.addOnNftSelectionChangeListener(this)
        observeWalletUpdate()
    }

    fun refresh() {
    }

    fun updateSelectionIndex(position: Int) {
        selectionIndexLiveData.value = position
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

    private fun loadSelectionCards(nftList: NFTListData? = null) {
        val nfts = (nftList?.nfts ?: cacheNftList().read()?.nfts) ?: return
        val data = cacheSelections().read() ?: NftSelections(mutableListOf())
        data.data = data.data.filter { selection -> nfts.firstOrNull { nft -> selection.uniqueId() == nft.uniqueId() } != null }.toMutableList()
        topSelectionLiveData.postValue(data)
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
        if (BuildConfig.DEBUG) {
            return "0xccea80173b51e028"
        }
        return cacheWallet.read()?.primaryWalletAddress()
    }

    private fun cacheNftList() = nftListCache(address())

    private fun cacheSelections() = nftSelectionCache(address())

    companion object {
        private val TAG = NFTFragmentViewModelV0::class.java.simpleName
    }
}