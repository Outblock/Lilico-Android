package io.outblock.lilico.page.nft.collectionlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.flowjvm.cadenceNftEnabled
import io.outblock.lilico.manager.nft.NftCollectionStateChangeListener
import io.outblock.lilico.manager.nft.NftCollectionStateManager
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.nft.collectionlist.model.NftCollectionItem
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.toast
import io.outblock.lilico.utils.viewModelIOScope

class NftCollectionListViewModel : ViewModel(), OnTransactionStateChange, NftCollectionStateChangeListener {

    val collectionListLiveData = MutableLiveData<List<NftCollectionItem>>()

    private val collectionList = mutableListOf<NftCollectionItem>()

    private var transactionIds = mutableListOf<String>()

    private var keyword = ""

    init {
        TransactionStateManager.addOnTransactionStateChange(this)
        NftCollectionStateManager.addListener(this)
    }

    fun load() {
        viewModelIOScope(this) {
            collectionList.clear()
            collectionList.addAll(
                NftCollectionConfig.list()
                    .filter { it.address(forceMainnet = false).isNotEmpty() }
                    .map { NftCollectionItem(collection = it, isAdded = NftCollectionStateManager.isTokenAdded(it.address()), isAdding = false) })
            collectionListLiveData.postValue(collectionList.toList())

            onTransactionStateChange()

            NftCollectionStateManager.fetchState()
        }
    }

    fun search(keyword: String) {
        this.keyword = keyword
        if (keyword.isBlank()) {
            collectionListLiveData.postValue(collectionList.toList())
        } else {
            collectionListLiveData.postValue(collectionList.filter { it.collection.name.lowercase().contains(keyword.lowercase()) })
        }
    }

    fun clearSearch() {
        this.keyword = ""
        search("")
    }

    fun addToken(collection: NftCollection) {
        viewModelIOScope(this) {
            val transactionId = cadenceNftEnabled(collection)
            if (transactionId.isNullOrBlank()) {
                toast(msgRes = R.string.add_token_failed)
            } else {
                val transactionState = TransactionState(
                    transactionId = transactionId,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_ENABLE_NFT,
                    data = Gson().toJson(collection)
                )
                TransactionStateManager.newTransaction(transactionState)
                pushBubbleStack(transactionState)
                transactionIds.add(transactionId)
                onTransactionStateChange()
            }
        }
    }

    override fun onTransactionStateChange() {
        viewModelIOScope(this) {
            val transactionList = TransactionStateManager.getTransactionStateList()
            transactionList.forEach { state ->
                if (state.type == TransactionState.TYPE_ENABLE_NFT) {
                    val coin = state.nftCollectionData()
                    val index = collectionList.indexOfFirst { it.collection.contractName == coin.contractName }
                    collectionList[index] = NftCollectionItem(
                        collection = collectionList[index].collection,
                        isAdding = state.isProcessing(),
                        isAdded = NftCollectionStateManager.isTokenAdded(coin.address())
                    )
                }
            }
            NftCollectionStateManager.fetchState()
            search(keyword)
        }
    }

    override fun onNftCollectionStateChange(collection: NftCollection, isEnable: Boolean) {
        onTransactionStateChange()
    }
}
