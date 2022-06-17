package io.outblock.lilico.firebase.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.outblock.lilico.R
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.utils.logd


fun initFirebaseConfig() {
    val config = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }
    config.setConfigSettingsAsync(configSettings)
    config.setDefaultsAsync(R.xml.remote_config_defaults).addOnCompleteListener {
        logd("initFirebaseConfig", "from local default")
        onConfigLoadFinish()
    }
    firebaseConfigFetch()
}

fun firebaseConfigFetch() {
    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
        onConfigLoadFinish()
    }
}

private fun onConfigLoadFinish() {
    FlowCoinListManager.reload()
    NftCollectionConfig.sync()
    GasConfig.sync()
}