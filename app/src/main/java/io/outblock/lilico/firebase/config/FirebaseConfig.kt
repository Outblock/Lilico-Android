package io.outblock.lilico.firebase.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.outblock.lilico.R
import io.outblock.lilico.manager.coin.FlowCoinListManager


fun initFirebaseConfig() {
    val config = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }
    config.setConfigSettingsAsync(configSettings)
    config.setDefaultsAsync(R.xml.remote_config_defaults).addOnCompleteListener { onConfigLoadFinish() }
    firebaseConfigFetch()
}

fun firebaseConfigFetch() {
    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
        onConfigLoadFinish()
    }
}

private fun onConfigLoadFinish() {
    FlowCoinListManager.reload()
}