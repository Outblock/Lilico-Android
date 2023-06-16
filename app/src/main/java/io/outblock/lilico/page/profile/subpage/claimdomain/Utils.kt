package io.outblock.lilico.page.profile.subpage.claimdomain

import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.page.address.FlowDomainServer
import io.outblock.lilico.page.address.queryAddressBookFromBlockchain
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.setMeowDomainClaimed
import java.lang.ref.WeakReference


private const val TAG = "ClaimDomainUtils"

private val listeners = mutableListOf<WeakReference<MeowDomainClaimedStateChangeListener>>()

fun observeMeowDomainClaimedStateChange(listener: MeowDomainClaimedStateChangeListener) {
    listeners.add(WeakReference(listener))
}

fun checkMeowDomainClaimed() {
    ioScope {
        val username = userInfoCache().read()?.username ?: return@ioScope
        val walletAddress = WalletManager.wallet()?.walletAddress() ?: return@ioScope
        val contact = queryAddressBookFromBlockchain(username, FlowDomainServer.MEOW) ?: return@ioScope
        setMeowDomainClaimed(contact.address == walletAddress)
        dispatchListeners(contact.address == walletAddress)
        logd(TAG, "meow domain claimed: ${contact.address == walletAddress}")
    }
}

interface MeowDomainClaimedStateChangeListener {
    fun onDomainClaimedStateChange(isClaimed: Boolean)
}

private fun dispatchListeners(isClaimed: Boolean) {
    listeners.removeIf { it.get() == null }
    listeners.forEach { it.get()?.onDomainClaimedStateChange(isClaimed) }
}