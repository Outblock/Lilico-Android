package io.outblock.lilico.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import io.outblock.lilico.network.model.Nft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val KEY_LAUNCH_TIMES = "KEY_LAUNCH_TIMES"

private val KEY_JWT_REFRESH_TIME = longPreferencesKey("KEY_JWT_REFRESH_TIME")
private val KEY_USERNAME = stringPreferencesKey("KEY_USERNAME")
private val KEY_REGISTERED = booleanPreferencesKey("KEY_REGISTERED")
private val KEY_NFT_SELECTIONS = stringPreferencesKey("KEY_NFT_SELECTIONS")
private val KEY_NFT_COLLECTION_EXPANDED = booleanPreferencesKey("KEY_NFT_COLLECTION_EXPANDED")
private val KEY_BIOMETRIC_ENABLE = booleanPreferencesKey("KEY_BIOMETRIC_ENABLE")

private val scope = CoroutineScope(Dispatchers.IO)

// At the top level of your kotlin file:
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "main_preference")
private val dataStore = Env.getApp().dataStore

suspend fun getJwtRefreshTime(): Long = dataStore.data.map { it[KEY_JWT_REFRESH_TIME] ?: 0 }.first()

fun updateJwtRefreshTime() {
    edit { dataStore.edit { it[KEY_JWT_REFRESH_TIME] = System.currentTimeMillis() } }
}

suspend fun getUsername(): String = dataStore.data.map { it[KEY_USERNAME].orEmpty() }.first()

fun updateUsername(username: String) {
    edit { dataStore.edit { it[KEY_USERNAME] = username } }
}

suspend fun isRegistered(): Boolean = dataStore.data.map { it[KEY_REGISTERED] ?: false }.first()

fun setRegistered() {
    edit { dataStore.edit { it[KEY_REGISTERED] = true } }
}

suspend fun isNftInSelection(nft: Nft): Boolean {
    val list = dataStore.data.map { it[KEY_NFT_SELECTIONS] }.first().orEmpty().split(",")
    return list.contains(nft.uniqueId())
}

suspend fun updateNftSelectionsPref(list: List<String>) {
    dataStore.edit { block -> block[KEY_NFT_SELECTIONS] = list.joinToString(",") { it } }
}

suspend fun isNftCollectionExpanded(): Boolean = dataStore.data.map { it[KEY_NFT_COLLECTION_EXPANDED] ?: false }.first()

suspend fun updateNftCollectionExpanded(isExpanded: Boolean) {
    dataStore.edit { it[KEY_NFT_COLLECTION_EXPANDED] = isExpanded }
}

suspend fun isBiometricEnable(): Boolean = dataStore.data.map { it[KEY_BIOMETRIC_ENABLE] ?: false }.first()

fun setBiometricEnable(isEnable: Boolean) {
    edit { dataStore.edit { it[KEY_BIOMETRIC_ENABLE] = isEnable } }
}


private fun edit(unit: suspend () -> Unit) {
    scope.launch { unit.invoke() }
}