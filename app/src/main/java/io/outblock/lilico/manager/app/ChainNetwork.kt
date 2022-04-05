package io.outblock.lilico.manager.app

import io.outblock.lilico.utils.*

private var network = if (isDev()) NETWORK_TESTNET else NETWORK_MAINNET
private var isDeveloperMode = false

fun refreshChainNetwork(callback: (() -> Unit)? = null) {
    cpuScope {
        logd("refreshChainNetwork", "start")
        isDeveloperMode = isDeveloperModeEnable()
        network = getChainNetworkPreference()
        uiScope { callback?.invoke() }
        logd("refreshChainNetwork", "end")
    }
}

fun chainNetwork() = network

fun isMainnet() = network == NETWORK_MAINNET || !isDeveloperMode
fun isTestnet() = network == NETWORK_TESTNET && isDeveloperMode