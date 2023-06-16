package io.outblock.lilico.manager.childaccount

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.cacheFile
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_LIST
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_META
import io.outblock.lilico.manager.flowjvm.executeCadence
import io.outblock.lilico.manager.flowjvm.parseAddressList
import io.outblock.lilico.utils.ioScope


class ChildAccountList(
    val address: String,
) {
    private val accountList = mutableListOf<ChildAccount>()

    init {
        ioScope { accountList.addAll(cache().read().orEmpty()) }
        refresh()
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

    fun refresh() {
        ioScope {
            val accounts = queryAccountMeta(address) ?: return@ioScope
            val oldAccounts = accountList.toList()
            accounts.forEach { account -> account.pinTime = (oldAccounts.firstOrNull { it.address == account.address }?.pinTime ?: 0) }
            accountList.clear()
            accountList.addAll(accounts)
            cache().cache(ChildAccountCache().apply { addAll(accountList) })
        }
    }

    private fun queryAccountList(): List<String> {
        val result = CADENCE_QUERY_CHILD_ACCOUNT_LIST.executeCadence { arg { address(address) } }
        return result.parseAddressList()
    }

    private fun queryAccountMeta(address: String): List<ChildAccount>? {
        val result = CADENCE_QUERY_CHILD_ACCOUNT_META.executeCadence { arg { address(address) } }
        return result?.parseAccountMetas()
    }

    private fun cache(): CacheManager<ChildAccountCache> {
        return CacheManager(
            "${address}.child_account_list".cacheFile(),
            ChildAccountCache::class.java,
        )
    }
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