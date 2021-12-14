package io.outblock.lilico.utils

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * 加密存储 SharedPreferences
 */

// 助记词
private const val KEY_MNEMONIC = "key_mnemonic"

private const val KEY_JWT_TOKEN = "jwt_token"

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

fun getMnemonic(): String = preference.getString(KEY_MNEMONIC, "").orEmpty()

fun saveJwtToken(jwt: String) {
    preference.edit().putString(KEY_JWT_TOKEN, jwt).apply()
}

fun getJwtToken(): String = preference.getString(KEY_JWT_TOKEN, "").orEmpty()