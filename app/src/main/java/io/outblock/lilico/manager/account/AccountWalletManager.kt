package io.outblock.lilico.manager.account

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.utils.DATA_PATH
import io.outblock.lilico.utils.getWalletStoreNameAesKey
import io.outblock.lilico.utils.readWalletPassword
import io.outblock.lilico.utils.saveWalletStoreNameAesKey
import io.outblock.lilico.utils.secret.aesEncrypt
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.accountPublicKeyCache
import io.outblock.lilico.manager.flowjvm.queryAccountPublicKey
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.wallet.getPublicKey
import wallet.core.jni.HDWallet
import wallet.core.jni.StoredKey
import java.io.File
import java.util.UUID

/**
 * Created by Mengxy on 8/30/23.
 */
object AccountWalletManager {

    private var publicKeyMap = mutableMapOf<String, String>()

    private fun passwordMap(): HashMap<String, String> {
        val pref = runCatching { readWalletPassword() }.getOrNull()
        return if (pref.isNullOrBlank()) {
            HashMap()
        } else {
            Gson().fromJson(pref, object : TypeToken<HashMap<String, String>>() {}.type)
        }
    }

    fun initAccountPublicKeyMap() {
        logd("AccountWalletManager", "init")
        publicKeyMap.clear()
        if (accountPublicKeyCache().isCacheExist()) {
            publicKeyMap.putAll(accountPublicKeyCache().read()?.keyMap ?: emptyMap())
        } else {
            queryAllAccountPublicKey()
        }
    }

    fun addPublicKey(address: String?, publicKey: String) {
        if (address.isNullOrBlank()) {
            return
        }
        if (publicKeyMap.containsKey(address)) {
            return
        }
        publicKeyMap[address] = publicKey
        accountPublicKeyCache().cache(AccountPublicKeyCache(publicKeyMap))
    }

    private fun queryAllAccountPublicKey() {
        ioScope {
            val keyMap = queryAccountPublicKey(AccountManager.addressList())
            accountPublicKeyCache().cache(AccountPublicKeyCache(keyMap))
            publicKeyMap.putAll(keyMap)
            logd("AccountWalletManager", publicKeyMap.toString())
        }
    }

    fun getHDWalletByAddress(address: String): HDWallet? {
        val publicKey = publicKeyMap[address]
        val hdWalletList = mutableListOf<HDWallet>()
        for ((uid, password) in passwordMap()) {
            hdWalletList.add(WalletStoreWithUid(uid, password).wallet())
        }
        return hdWalletList.firstOrNull {
            it.getPublicKey() == publicKey
        }
    }

}


class WalletStoreWithUid(private val uid: String, private val password: String) {
    private var keyStore: StoredKey

    init {
        keyStore = generateKeyStore()
    }

    fun wallet(): HDWallet = keyStore.wallet(password.hexToBytes())

    private fun generateKeyStore(): StoredKey {
        return if (!File(storePath()).exists()) {
            StoredKey(storeName(), password.hexToBytes())
        } else {
            StoredKey.load(storePath())
        }
    }

    private fun storePath() = File(DATA_PATH, storeName()).absolutePath

    private fun storeName() = aesEncrypt(key = storeNameAesKey(), message = uid)

    private fun storeNameAesKey(): String {
        var local = getWalletStoreNameAesKey()
        if (local.isBlank()) {
            local = randomString()
            saveWalletStoreNameAesKey(local)
        }
        return local
    }

    private fun randomString(length: Int = 16): String = UUID.randomUUID().toString().take(length)
}

class AccountPublicKeyCache(
    @SerializedName("keyMap")
    val keyMap: Map<String, String> = emptyMap()
)
