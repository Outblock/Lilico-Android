package io.outblock.lilico.utils

import android.content.Context
import android.graphics.Point
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.page.token.detail.QuoteMarket
import io.outblock.lilico.utils.extensions.toSafeInt
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

private val KEY_BACKUP_MANUALLY = booleanPreferencesKey("KEY_BACKUP_MANUALLY")
private val KEY_BACKUP_GOOGLE_DRIVE = booleanPreferencesKey("KEY_BACKUP_GOOGLE_DRIVE")
private val KEY_SEND_STATE_BUBBLE_POSITION = stringPreferencesKey("KEY_SEND_STATE_BUBBLE_POSITION")
private val KEY_DEVELOPER_MODE_ENABLE = booleanPreferencesKey("KEY_DEVELOPER_MODE_ENABLE")
private val KEY_CHAIN_NETWORK = intPreferencesKey("KEY_CHAIN_NETWORK")
private val KEY_THEME_MODE = intPreferencesKey("KEY_THEME_MODE")
private val KEY_QUOTE_MARKET = stringPreferencesKey("KEY_QUOTE_MARKET")

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

suspend fun isBackupManually(): Boolean = dataStore.data.map { it[KEY_BACKUP_MANUALLY] ?: false }.first()

fun setBackupManually() {
    edit { dataStore.edit { it[KEY_BACKUP_MANUALLY] = true } }
}

suspend fun isBackupGoogleDrive(): Boolean = dataStore.data.map { it[KEY_BACKUP_GOOGLE_DRIVE] ?: false }.first()

fun setBackupGoogleDrive(isBackuped: Boolean = true) {
    edit { dataStore.edit { it[KEY_BACKUP_GOOGLE_DRIVE] = isBackuped } }
}

suspend fun isDeveloperModeEnable(): Boolean = dataStore.data.map { it[KEY_DEVELOPER_MODE_ENABLE] ?: isDev() }.first()

fun setDeveloperModeEnable(isEnable: Boolean) {
    edit { dataStore.edit { it[KEY_DEVELOPER_MODE_ENABLE] = isEnable } }
}

suspend fun getChainNetworkPreference(): Int =
    dataStore.data.map { it[KEY_CHAIN_NETWORK] ?: if (isDev()) NETWORK_TESTNET else NETWORK_MAINNET }.first()

fun updateChainNetworkPreference(network: Int, callback: (() -> Unit)? = null) {
    edit {
        dataStore.edit { it[KEY_CHAIN_NETWORK] = network }
        callback?.invoke()
    }
}

suspend fun getSendStateBubblePosition(): Point {
    val list = dataStore.data
        .map { it[KEY_SEND_STATE_BUBBLE_POSITION] ?: "${Gravity.END},${(ScreenUtils.getScreenHeight() * 0.5f).toInt()}" }
        .first().split(",").map { it.toSafeInt() }
    return Point(list[0], list[1])
}

fun updateSendStateBubblePosition(point: Point) {
    edit { dataStore.edit { it[KEY_SEND_STATE_BUBBLE_POSITION] = "${point.x},${point.y}" } }
}

suspend fun getThemeMode(): Int = dataStore.data.map { it[KEY_THEME_MODE] ?: AppCompatDelegate.MODE_NIGHT_NO }.first()

fun updateThemeMode(themeMode: Int) {
    edit { dataStore.edit { it[KEY_THEME_MODE] = themeMode } }
}

suspend fun getQuoteMarket(): String = dataStore.data.map { it[KEY_QUOTE_MARKET] ?: QuoteMarket.binance.value }.first()

suspend fun updateQuoteMarket(market: String) {
    dataStore.edit { it[KEY_QUOTE_MARKET] = market }
}


private fun edit(unit: suspend () -> Unit) {
    scope.launch { unit.invoke() }
}