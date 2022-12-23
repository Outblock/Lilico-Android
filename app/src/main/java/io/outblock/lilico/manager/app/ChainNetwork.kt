package io.outblock.lilico.manager.app

import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.utils.*

private var network = if (isDev()) NETWORK_TESTNET else NETWORK_TESTNET
private var isDeveloperMode = false

fun refreshChainNetwork(callback: (() -> Unit)? = null) {
    cpuScope {
        refreshChainNetworkSync()
        uiScope { callback?.invoke() }
    }
}

suspend fun refreshChainNetworkSync() {
    logd("refreshChainNetwork", "start")
    isDeveloperMode = isDeveloperModeEnable()
    network = getChainNetworkPreference()
    logd("refreshChainNetwork", "end")
}

fun chainNetwork() = network

fun isMainnet() = network == NETWORK_MAINNET
fun isTestnet() = network == NETWORK_TESTNET

fun chainNetWorkString(): String {
    return if (isTestnet()) "testnet" else "mainnet"
}

fun doNetworkChangeTask() {
    NftCollectionConfig.sync()
}