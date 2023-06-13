package io.outblock.lilico.manager.account

import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd

// from single account to multi account
fun accountMigrateV1(callback: (() -> Unit)? = null) {
    ioScope {
        if (!isAccountV1DataExist()) {
            callback?.invoke()
            return@ioScope
        }

        migrateV1()
    }
}

fun migrateV1() {
    logd("xxx", "migrate start")
    val account = Account(
        userInfo = userInfoCache().read()!!,
        isActive = true,
        wallet = walletCache().read()
    )
    AccountManager.add(account)
    userInfoCache().clear()
    logd("xxx", "migrate end username:${account.userInfo.username}")
}

suspend fun isAccountV1DataExist() = userInfoCache().isCacheExist()