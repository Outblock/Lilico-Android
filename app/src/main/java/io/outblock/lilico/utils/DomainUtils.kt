package io.outblock.lilico.utils

import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.page.address.FlowDomainServer


fun meowDomain(): String? {
    val username = userInfoCache().read()?.username ?: return null
    return "$username.${FlowDomainServer.MEOW.domain}"
}

fun meowDomainHost(): String? {
    return userInfoCache().read()?.username
}