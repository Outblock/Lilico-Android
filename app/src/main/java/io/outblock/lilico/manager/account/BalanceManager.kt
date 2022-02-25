package io.outblock.lilico.manager.account

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.AddressInfoAccount
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.toAddress
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object BalanceManager {
    private val TAG = BalanceManager::class.java.simpleName

    private val listeners = CopyOnWriteArrayList<WeakReference<OnBalanceUpdate>>()

    private val balanceList = CopyOnWriteArrayList<AddressInfoAccount>()

    private val cache by lazy { CacheManager("BALANCE_CACHE", BalanceCache::class.java) }

    fun init() {
        ioScope {
            balanceList.addAll(cache.read()?.data ?: emptyList())

            val blockchainList = walletCache().read()?.primaryWallet()?.blockchain ?: return@ioScope
            for (blockchain in blockchainList) {
                fetch(blockchain.address.toAddress())
            }
        }
    }

    suspend fun getBalanceByAddress(address: String) {
        balanceList.firstOrNull { it.address.toAddress() == address.toAddress() }?.let { dispatchListeners(it) }
        fetch(address)
    }

    fun addListener(callback: OnBalanceUpdate) {
        uiScope { this.listeners.add(WeakReference(callback)) }
    }

    private fun dispatchListeners(balance: AddressInfoAccount) {
        logd(TAG, "dispatchListeners:$balance")
        uiScope {
            listeners.removeAll { it.get() == null }
            listeners.forEach { it.get()?.onBalanceUpdate(balance) }
        }
    }

    fun getBalanceList() = balanceList.toList()

    private suspend fun fetch(address: String) {
        val service = retrofit().create(ApiService::class.java)
        val resp = service.getAddressInfo(address.toAddress())
        if (resp.status == 200) {
            val balance = resp.data.data.account
            val existBalance = balanceList.firstOrNull { it.address.toAddress() == balance.address.toAddress() }
            val isDiff = balanceList.isEmpty() || existBalance == null || existBalance.balance != balance.balance
            if (isDiff) {
                dispatchListeners(balance)
                balanceList.removeAll { it.address.toAddress() == address.toAddress() }
                balanceList.add(balance)
                ioScope { cache.cache(BalanceCache(balanceList.toList())) }
            }
        }
    }
}

interface OnBalanceUpdate {
    fun onBalanceUpdate(balance: AddressInfoAccount)
}

private class BalanceCache(
    @SerializedName("data")
    val data: List<AddressInfoAccount>,
)

