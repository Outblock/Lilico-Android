package io.outblock.lilico.utils

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Crypto SharedPreferences
 */

// mnemonic
private const val KEY_MNEMONIC = "key_mnemonic"
private const val KEY_MNEMONIC_BIOMETRIC = "key_mnemonic_biometric"

private const val KEY_JWT_TOKEN = "jwt_token"

private const val KEY_AES_LOCAL_CODE = "key_aes_local_code"

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

fun getMnemonicFromPreference(): String = preference.getString(KEY_MNEMONIC, "").orEmpty()

fun updateAesLocalCode(key: String) {
    preference.edit().putString(KEY_AES_LOCAL_CODE, key).apply()
}

fun getAesLocalCode(): String = preference.getString(KEY_AES_LOCAL_CODE, "").orEmpty()

fun saveJwtToken(jwt: String) {
    preference.edit().putString(KEY_JWT_TOKEN, jwt).apply()
}

fun clearJwtToken() {
    saveJwtToken("")
}

fun getJwtToken(): String = preference.getString(KEY_JWT_TOKEN, "").orEmpty()