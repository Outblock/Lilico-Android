package io.outblock.lilico.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val KEY_LAUNCH_TIMES = "KEY_LAUNCH_TIMES"

private val KEY_JWT_REFRESH_TIME = longPreferencesKey("KEY_JWT_REFRESH_TIME")
private val KEY_USERNAME = stringPreferencesKey("KEY_USERNAME")

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

private fun edit(unit: suspend () -> Unit) {
    scope.launch { unit.invoke() }
}