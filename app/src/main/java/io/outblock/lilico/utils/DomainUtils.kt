package io.outblock.lilico.utils

import io.outblock.lilico.manager.account.AccountManager
import io.outblock.lilico.page.address.FlowDomainServer


fun meowDomain(): String? {
    val username = AccountManager.userInfo()?.username ?: return null
    return "$username.${FlowDomainServer.MEOW.domain}"
}

fun meowDomainHost(): String? {
    return AccountManager.userInfo()?.username
}