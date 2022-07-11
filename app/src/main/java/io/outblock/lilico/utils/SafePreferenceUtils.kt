package io.outblock.lilico.utils

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Crypto SharedPreferences
 */

// mnemonic
private const val KEY_MNEMONIC = "key_mnemonic"
private const val KEY_PUSH_TOKEN = "push_token"
private const val KEY_WALLET_PASSWORD = "key_wallet_password"
private const val KEY_PIN_CODE = "key_pin_code"
private const val KEY_WALLET_STORE_NAME_AES_KEY = "key_wallet_store_name_aes_key"

private val preference by lazy {
    EncryptedSharedPreferences.create(
        Env.getApp(),
        "safe_preference",
        MasterKey.Builder(Env.getApp()).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
}

fun saveMnemonic(mnemonic: String) {
    preference.edit().putString(KEY_MNEMONIC, mnemonic).apply()
}

fun storeWalletPassword(key: String) {
    preference.edit().putString(KEY_WALLET_PASSWORD, key).apply()
}

fun readWalletPassword(): String = preference.getString(KEY_WALLET_PASSWORD, "").orEmpty()

@SuppressLint("ApplySharedPref")
@WorkerThread
fun savePinCode(key: String) {
    preference.edit().putString(KEY_PIN_CODE, key).apply()
}

fun getPinCode(): String = preference.getString(KEY_PIN_CODE, "").orEmpty()

fun getPushToken(): String = preference.getString(KEY_PUSH_TOKEN, "").orEmpty()

fun updatePushToken(token: String) {
    preference.edit().putString(KEY_PUSH_TOKEN, token).apply()
}

fun saveWalletStoreNameAesKey(key: String) {
    preference.edit().putString(KEY_WALLET_STORE_NAME_AES_KEY, key).apply()
}

fun getWalletStoreNameAesKey(): String = preference.getString(KEY_WALLET_STORE_NAME_AES_KEY, "").orEmpty()