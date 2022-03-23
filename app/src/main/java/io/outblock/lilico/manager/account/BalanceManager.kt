package io.outblock.lilico.manager.account

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.manager.flowjvm.cadenceQueryTokenBalance
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object BalanceManager {
    private val TAG = BalanceManager::class.java.simpleName

    private val listeners = CopyOnWriteArrayList<WeakReference<OnBalanceUpdate>>()

    private val balanceList = CopyOnWriteArrayList<Balance>()

    private val cache by lazy { CacheManager("BALANCE_CACHE_v1.0", BalanceCache::class.java) }

    fun init() {
        ioScope {
            balanceList.addAll(cache.read()?.data ?: emptyList())

            FlowCoinListManager.coinList().filter { TokenStateManager.isTokenAdded(it.address()) }.forEach { fetch(it) }
        }
    }

    fun getBalanceByCoin(coin: FlowCoin) {
        logd(TAG, "getBalanceByCoin:${coin.symbol}")
        balanceList.firstOrNull { it.symbol == coin.symbol }?.let { dispatchListeners(coin, it.balance) }
        fetch(coin)
    }

    fun addListener(callback: OnBalanceUpdate) {
        if (listeners.firstOrNull { it.get() == callback } != null) {
            return
        }
        uiScope { this.listeners.add(WeakReference(callback)) }
    }

    private fun dispatchListeners(coin: FlowCoin, balance: Float) {
        logd(TAG, "dispatchListeners ${coin.symbol}:$balance")
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.forEach { it.get()?.onBalanceUpdate(Balance(coin.symbol, balance)) }
        }
    }

    fun getBalanceList() = balanceList.toList()

    private fun fetch(coin: FlowCoin) {
        ioScope {
            val balance = cadenceQueryTokenBalance(coin)
            if (balance != null) {
                val existBalance = balanceList.firstOrNull { coin.symbol == it.symbol }
                val isDiff = balanceList.isEmpty() || existBalance == null || existBalance.balance != balance
                if (isDiff) {
                    dispatchListeners(coin, balance)
                    balanceList.removeAll { it.symbol == coin.symbol }
                    balanceList.add(Balance(coin.symbol, balance))
                    ioScope { cache.cache(BalanceCache(balanceList.toList())) }
                }
            }
        }
    }
}

interface OnBalanceUpdate {
    fun onBalanceUpdate(balance: Balance)
}

data class Balance(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("balance")
    val balance: Float,
)

private class BalanceCache(
    @SerializedName("data")
    val data: List<Balance>,
)

