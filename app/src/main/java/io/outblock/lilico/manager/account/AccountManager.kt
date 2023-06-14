package io.outblock.lilico.manager.account

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.clearCacheDir
import io.outblock.lilico.utils.uiScope

object AccountManager {
    private val accounts = mutableListOf<Account>()

    fun init() {
        accountsCache().read()?.let { accounts.addAll(it) }
    }

    fun add(account: Account) {
        accounts.removeAll { it.userInfo.username == account.userInfo.username }
        accounts.add(account)
        accounts.forEach { it.isActive = it == account }
        accountsCache().cache(Accounts().apply { addAll(accounts) })
    }

    fun get() = accounts.firstOrNull { it.isActive }

    fun userInfo() = get()?.userInfo

    fun updateUserInfo(userInfo: UserInfoData) {
        get()?.userInfo = userInfo
        accountsCache().cache(Accounts().apply { addAll(accounts) })
    }

    fun list() = accounts.toList()

    fun switch(account: Account) {
        // TODO
        accounts.forEach { it.isActive = it == account }
        clearCacheDir()
        uiScope { MainActivity.relaunch(Env.getApp(), true) }
    }
}

fun username() = AccountManager.get()!!.userInfo.username

data class Account(
    @SerializedName("username")
    var userInfo: UserInfoData,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("wallet")
    val wallet: WalletListData? = null,
)

class Accounts : ArrayList<Account>()

fun accountsCache(): CacheManager<Accounts> {
    return CacheManager("${"accounts".hashCode()}", Accounts::class.java)
}