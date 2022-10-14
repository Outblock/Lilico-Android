package io.outblock.lilico.manager.nft

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.nftCollectionStateCache
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.flowjvm.cadenceNftCheckEnabled
import io.outblock.lilico.manager.flowjvm.cadenceNftListCheckEnabled
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.logw
import io.outblock.lilico.utils.uiScope
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object NftCollectionStateManager {
    private val TAG = NftCollectionStateManager::class.java.simpleName

    private val tokenStateList = CopyOnWriteArrayList<NftCollectionState>()
    private val listeners = CopyOnWriteArrayList<WeakReference<NftCollectionStateChangeListener>>()

    fun reload() {
        ioScope {
            tokenStateList.clear()
            tokenStateList.addAll(nftCollectionStateCache().read()?.stateList ?: emptyList())
        }
    }

    fun fetchState(onFinish: (() -> Unit)? = null) {
        ioScope { fetchStateSync(onFinish) }
    }

    private fun fetchStateSync(onFinish: (() -> Unit)? = null) {
        val collectionList = NftCollectionConfig.list()
        val isEnableList = cadenceNftListCheckEnabled(collectionList)
        if (collectionList.size != isEnableList?.size) {
            logw(TAG, "fetch error")
            return
        }
        collectionList.forEachIndexed { index, collection ->
            val isEnable = isEnableList[index]
            val oldState = tokenStateList.firstOrNull { it.address == collection.address }
            tokenStateList.remove(oldState)
            tokenStateList.add(NftCollectionState(collection.name, collection.address, isEnable))
            if (oldState?.isAdded != isEnable) {
                dispatchListeners(collection, isEnable)
            }
        }
        nftCollectionStateCache().cache(NftCollectionStateCache(tokenStateList.toList()))
        onFinish?.invoke()
    }

    fun fetchStateSingle(collection: NftCollection, cache: Boolean = false) {
        val isEnable = cadenceNftCheckEnabled(collection)
        if (isEnable != null) {
            val oldState = tokenStateList.firstOrNull { it.name == collection.name }
            tokenStateList.remove(oldState)
            tokenStateList.add(NftCollectionState(collection.name, collection.address, isEnable))
            if (oldState?.isAdded != isEnable) {
                dispatchListeners(collection, isEnable)
            }
        }
        if (cache) {
            nftCollectionStateCache().cache(NftCollectionStateCache(tokenStateList.toList()))
        }
    }

    fun isTokenAdded(tokenAddress: String) = tokenStateList.firstOrNull { it.address == tokenAddress }?.isAdded ?: false

    fun addListener(callback: NftCollectionStateChangeListener) {
        uiScope { this.listeners.add(WeakReference(callback)) }
    }

    private fun dispatchListeners(collection: NftCollection, isEnable: Boolean) {
        logd(TAG, "${collection.name} isEnable:$isEnable")
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.toList().forEach { it.get()?.onNftCollectionStateChange(collection, isEnable) }
        }
    }
}

interface NftCollectionStateChangeListener {
    fun onNftCollectionStateChange(collection: NftCollection, isEnable: Boolean)
}

class NftCollectionStateCache(
    @SerializedName("stateList")
    val stateList: List<NftCollectionState> = emptyList(),
)

class NftCollectionState(
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("isAdded")
    val isAdded: Boolean,
)