package io.outblock.lilico.manager.nft

import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.nft.nftlist.isSameNft
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.updateNftSelectionsPref
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object NftSelectionManager {

    private val listeners = CopyOnWriteArrayList<WeakReference<OnNftSelectionChangeListener>>()

    private fun cache() = nftSelectionCache(walletCache().read()?.primaryWalletAddress())

    fun addOnNftSelectionChangeListener(listener: OnNftSelectionChangeListener) {
        listeners.add(WeakReference(listener))
    }

    fun addNftToSelection(nft: Nft) {
        ioScope {
            val selections = cache().read() ?: NftSelections(data = mutableListOf())
            val list = selections.data.toMutableList()
            val isExist = list.firstOrNull { it.contract.address == nft.contract.address && it.id.tokenId == nft.id.tokenId } != null

            if (!isExist) {
                list.add(nft)
            }
            selections.data = list
            cache().cache(selections)
            updateNftSelectionsPref(list.map { it.uniqueId() })
            dispatchListener(true, nft)
        }
    }

    fun removeNftFromSelection(nft: Nft) {
        ioScope {
            val selections = cache().read() ?: NftSelections(data = mutableListOf())
            val list = selections.data.toMutableList()

            list.removeIf { it.isSameNft(nft) }
            selections.data = list
            cache().cache(selections)

            updateNftSelectionsPref(list.map { it.uniqueId() })
            dispatchListener(false, nft)
        }
    }

    private fun dispatchListener(isAdd: Boolean, nft: Nft) {
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.forEach {
                val listener = it.get() ?: return@forEach
                if (isAdd) {
                    listener.onAddSelection(nft)
                } else {
                    listener.onRemoveSelection(nft)
                }
            }
        }
    }
}

interface OnNftSelectionChangeListener {
    fun onAddSelection(nft: Nft)

    fun onRemoveSelection(nft: Nft)
}