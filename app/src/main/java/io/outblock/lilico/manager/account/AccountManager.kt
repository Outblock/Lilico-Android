package io.outblock.lilico.manager.account

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.model.WalletListData

object AccountManager {
    private val accounts = mutableListOf<Account>()

    fun init() {
        accountsCache().read()?.let { accounts.addAll(it) }
    }

    fun add(account: Account) {
        accounts.removeAll { it.userInfo.username == account.userInfo.username }
        accounts.add(account)
        accountsCache().cache(Accounts().apply { addAll(accounts) })
    }

    fun get() = accounts.firstOrNull { it.isActive }

    fun userInfo() = get()?.userInfo

    fun updateUserInfo(userInfo: UserInfoData) {
        get()?.userInfo = userInfo
        accountsCache().cache(Accounts().apply { addAll(accounts) })
    }
}

fun username() = AccountManager.get()!!.userInfo.username

data class Account(
    @SerializedName("username")
    var userInfo: UserInfoData,
    @SerializedName("isActive")
    val isActive: Boolean = false,
    @SerializedName("wallet")
    val wallet: WalletListData? = null,
)

class Accounts : ArrayList<Account>()

fun accountsCache(): CacheManager<Accounts> {
    return CacheManager("${"accounts".hashCode()}", Accounts::class.java)
}