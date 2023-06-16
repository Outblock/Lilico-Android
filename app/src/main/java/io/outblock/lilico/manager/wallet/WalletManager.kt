package io.outblock.lilico.manager.wallet

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.cache.CACHE_WALLET
import io.outblock.lilico.cache.CacheManager
import io.outblock.lilico.cache.cacheFile
import io.outblock.lilico.manager.app.chainNetWorkString
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.childaccount.ChildAccountList
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.utils.getSelectedWalletAddress
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.updateSelectedWalletAddress

object WalletManager {

    private var wallet: WalletListData? = null

    private var childAccountMap = mapOf<String, ChildAccountList>()

    fun init() {
        ioScope { wallet = cache().read()?.apply { refreshChildAccount(this) } }
    }

    fun wallet() = wallet

    fun childAccountList(walletAddress: String? = null): ChildAccountList? {
        val address = (walletAddress ?: wallet?.walletAddress()) ?: return null
        return childAccountMap[address]
    }

    fun childAccount(childAddress: String): ChildAccount? {
        return childAccountMap.toMap().values.flatMap { it.get() }.firstOrNull { it.address == childAddress }
    }

    fun update(wallet: WalletListData) {
        this.wallet = wallet.apply { refreshChildAccount(this) }
        cache().cache(wallet)
    }

    fun refreshChildAccount() {
        childAccountMap.values.forEach { it.refresh() }
    }

    fun selectedWalletAddress(): String {
        val pref = getSelectedWalletAddress()
        val isExist = childAccountMap.keys.contains(pref) || childAccount(pref.orEmpty()) != null
        if (isExist) {
            return pref.orEmpty()
        }

        return wallet?.walletAddress().orEmpty().apply {
            if (isNotBlank()) {
                updateSelectedWalletAddress(this)
            }
        }
    }

    private fun cache(): CacheManager<WalletListData> {
        return CacheManager(
            "$CACHE_WALLET-${chainNetWorkString()}-${Firebase.auth.currentUser?.uid}".cacheFile(),
            WalletListData::class.java,
        )
    }

    private fun refreshChildAccount(wallet: WalletListData) {
        childAccountMap = wallet.wallets?.associate {
            it.address().orEmpty() to ChildAccountList(it.address().orEmpty())
        }.orEmpty().filter {
            it.key.isNotBlank()
        }
    }
}