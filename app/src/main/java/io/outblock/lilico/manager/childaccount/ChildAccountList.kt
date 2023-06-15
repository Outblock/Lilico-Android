package io.outblock.lilico.manager.childaccount

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.cacheFile
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_LIST
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_META
import io.outblock.lilico.manager.flowjvm.executeCadence
import io.outblock.lilico.manager.flowjvm.parseAddressList
import io.outblock.lilico.utils.ioScope


object ChildAccountList {
    private val accountList = mutableListOf<ChildAccount>()

    fun init() {
        ioScope {
            accountList.clear()
            accountList.addAll(cache().read().orEmpty())
        }
    }

    fun refresh() {
        ioScope {
            val accounts = queryAccountMeta(wallet().orEmpty()) ?: return@ioScope
            val oldAccounts = accountList.toList()
            accounts.forEach { account -> account.pinTime = (oldAccounts.firstOrNull { it.address == account.address }?.pinTime ?: 0) }
            accountList.clear()
            accountList.addAll(accounts)
            cache().cache(ChildAccountCache().apply { addAll(accountList) })
        }
    }

    fun get() = accountList.toList()

    fun togglePin(account: ChildAccount) {
        val localAccount = accountList.toList().firstOrNull { it.address == account.address } ?: return
        if (localAccount.pinTime > 0) {
            localAccount.pinTime = 0
        } else {
            localAccount.pinTime = System.currentTimeMillis()
        }

        cache().cache(ChildAccountCache().apply { addAll(accountList) })
    }

    private fun queryAccountList(): List<String> {
        val result = CADENCE_QUERY_CHILD_ACCOUNT_LIST.executeCadence { arg { address(wallet().orEmpty()) } }
        return result.parseAddressList()
    }

    private fun queryAccountMeta(address: String): List<ChildAccount>? {
        val result = CADENCE_QUERY_CHILD_ACCOUNT_META.executeCadence { arg { address(address) } }
        return result?.parseAccountMetas()
    }

    private fun cache(): CacheManager<ChildAccountCache> {
        return CacheManager(
            "${walletCache().read()?.walletAddress()}.child_account_list".cacheFile(),
            ChildAccountCache::class.java,
        )
    }

    private fun wallet() = walletCache().read()?.walletAddress()

}

data class ChildAccount(
    @SerializedName("address")
    val address: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("pinTime")
    var pinTime: Long = 0,
)

private class ChildAccountCache : ArrayList<ChildAccount>()