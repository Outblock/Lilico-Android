package io.outblock.lilico.wallet

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.bytesToHex
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.firebase.auth.isAnonymousSignIn
import io.outblock.lilico.utils.*
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet
import wallet.core.jni.StoredKey
import java.io.File
import java.util.*

private val TAG = WalletStore::class.java.simpleName

private const val TEMP_STORE = "temp"

object Wallet {

    private val store by lazy { WalletStore() }

    fun store() = store
}

class WalletStore internal constructor() {

    private var keyStore: StoredKey
    private val password: ByteArray

    init {
        password = password()
        keyStore = generateKeyStore()
    }

    fun updateMnemonic(mnemonic: String) = apply {
        logd(TAG, "updateMnemonic")
        keyStore = keyStore.changeMnemonic(mnemonic, password)
    }

    fun store() = apply {
        if (uid().isNullOrBlank()) {
            logw(TAG, "user not sign in, can't store")
            return@apply
        }
        logd(TAG, "store")

        if (keyStore.name() == storeName()) {
            keyStore = keyStore.changeName(storeName(), password)
        }

        saveCurrentUserPassword(password.bytesToHex())
        keyStore.store(storePath())
    }

    fun mnemonic(): String = keyStore.decryptMnemonic(password)

    fun wallet(): HDWallet = keyStore.wallet(password)

    private fun generateKeyStore(): StoredKey {
        val uid = uid()
        return if (uid.isNullOrBlank() || !File(storePath()).exists()) {
            StoredKey(TEMP_STORE, password)
        } else {
            StoredKey.load(storePath())
        }
    }

    private fun password() = readCurrentUserPassword()?.hexToBytes() ?: randomString().toByteArray()

    private fun storePath() = File(DATA_PATH, storeName()).absolutePath

    private fun storeName() = uid()!!.toByteArray().bytesToHex()
}

private fun StoredKey.changeName(name: String, password: ByteArray): StoredKey {
    return StoredKey.importHDWallet(decryptMnemonic(password), name, password, CoinType.FLOW)
}

private fun StoredKey.changeMnemonic(mnemonic: String, password: ByteArray): StoredKey {
    return StoredKey.importHDWallet(mnemonic, name(), password, CoinType.FLOW)
}

private fun readCurrentUserPassword(): String? {
    val uid = uid() ?: return null
    return passwordMap()[uid]
}

private fun saveCurrentUserPassword(password: String) {
    val uid = uid() ?: return
    val passwordMap = passwordMap()
    passwordMap[uid] = password
    storeWalletPassword(Gson().toJson(passwordMap))
}

private fun passwordMap(): HashMap<String, String> {
    val pref = readWalletPassword()
    return if (pref.isBlank()) {
        HashMap<String, String>()
    } else {
        Gson().fromJson(pref, object : TypeToken<HashMap<String, String>>() {}.type)
    }
}

private fun randomString(length: Int = 16): String = UUID.randomUUID().toString().take(length)

private fun uid() = if (isAnonymousSignIn()) null else Firebase.auth.currentUser?.uid