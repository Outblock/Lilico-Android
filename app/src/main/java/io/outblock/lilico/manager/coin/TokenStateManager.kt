package io.outblock.lilico.manager.coin

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.tokenStateCache
import io.outblock.lilico.manager.flowjvm.cadenceCheckTokenEnabled
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object TokenStateManager {
    private val TAG = TokenStateManager::class.java.simpleName

    private val tokenStateList = CopyOnWriteArrayList<TokenState>()
    private val listeners = CopyOnWriteArrayList<WeakReference<TokenStateChangeListener>>()

    fun init() {
        ioScope {
            tokenStateList.clear()
            tokenStateList.addAll(tokenStateCache().read()?.stateList ?: emptyList())
        }
    }

    fun fetchState() {
        ioScope {
            FlowCoinListManager.coinList().toList().forEach { coin -> fetchStateSingle(coin) }
            tokenStateCache().cache(TokenStateCache(tokenStateList.toList()))
        }
    }

    fun fetchStateSingle(coin: FlowCoin, cache: Boolean = false) {
        val isEnable = cadenceCheckTokenEnabled(coin)
        if (isEnable != null) {
            val oldState = tokenStateList.firstOrNull { it.symbol == coin.symbol }
            tokenStateList.remove(oldState)
            tokenStateList.add(TokenState(coin.symbol, coin.address(), isEnable))
            if (oldState?.isAdded != isEnable) {
                dispatchListeners(coin, isEnable)
            }
        }
        if (cache) {
            tokenStateCache().cache(TokenStateCache(tokenStateList.toList()))
        }
    }

    fun isTokenAdded(tokenAddress: String) = tokenStateList.firstOrNull { it.address == tokenAddress }?.isAdded ?: false

    fun addListener(callback: TokenStateChangeListener) {
        uiScope { this.listeners.add(WeakReference(callback)) }
    }

    private fun dispatchListeners(coin: FlowCoin, isEnable: Boolean) {
        logd(TAG, "${coin.name} isEnable:$isEnable")
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.toList().forEach { it.get()?.onTokenStateChange(coin, isEnable) }
        }
    }
}

interface TokenStateChangeListener {
    fun onTokenStateChange(coin: FlowCoin, isEnable: Boolean)
}

class TokenStateCache(
    @SerializedName("stateList")
    val stateList: List<TokenState> = emptyList(),
)

class TokenState(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("isAdded")
    val isAdded: Boolean,
)