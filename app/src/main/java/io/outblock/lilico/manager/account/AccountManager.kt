package io.outblock.lilico.manager.account

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.network.clearUserCache
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.Env
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

    fun get() = accounts.toList().firstOrNull { it.isActive }

    fun userInfo() = get()?.userInfo

    fun updateUserInfo(userInfo: UserInfoData) {
        list().firstOrNull { it.userInfo.username == userInfo.username }?.userInfo = userInfo
        accountsCache().cache(Accounts().apply { addAll(accounts) })
    }

    fun updateWalletInfo(wallet: WalletListData) {
        list().firstOrNull { it.userInfo.username == wallet.username }?.wallet = wallet
        accountsCache().cache(Accounts().apply { addAll(accounts) })
    }

    fun list() = accounts.toList()

    fun switch(account: Account) {
        uiScope {
            accounts.forEach {
                it.isActive = it.userInfo.username == account.userInfo.username
            }
            accountsCache().cache(Accounts().apply { addAll(accounts) })
            clearUserCache()
            uiScope { MainActivity.relaunch(Env.getApp(), true) }
        }
    }
}

fun username() = AccountManager.get()!!.userInfo.username

data class Account(
    @SerializedName("username")
    var userInfo: UserInfoData,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("wallet")
    var wallet: WalletListData? = null,
)

class Accounts : ArrayList<Account>()

fun accountsCache(): CacheManager<Accounts> {
    return CacheManager("${"accounts".hashCode()}", Accounts::class.java)
}