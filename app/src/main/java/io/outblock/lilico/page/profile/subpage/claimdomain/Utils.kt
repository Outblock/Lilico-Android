package io.outblock.lilico.page.profile.subpage.claimdomain

import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.page.address.FlowDomainServer
import io.outblock.lilico.page.address.queryAddressBookFromBlockchain
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.setMeowDomainClaimed


private const val TAG = "ClaimDomainUtils"

fun checkMeowDomainClaimed() {
    ioScope {
        val username = userInfoCache().read()?.username ?: return@ioScope
        val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return@ioScope
        val contact = queryAddressBookFromBlockchain(username, FlowDomainServer.MEOW) ?: return@ioScope
        setMeowDomainClaimed(contact.address == walletAddress)
        logd(TAG, "meow domain claimed: ${contact.address == walletAddress}")
    }
}