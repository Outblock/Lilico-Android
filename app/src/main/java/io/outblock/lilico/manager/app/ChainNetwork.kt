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
fun isSandboxNet() = network == NETWORK_SANDBOX


fun chainNetWorkString(): String {
    return when {
        isTestnet() -> NETWORK_NAME_TESTNET
        isSandboxNet() -> NETWORK_NAME_SANDBOX
        else -> NETWORK_NAME_MAINNET
    }
}

fun doNetworkChangeTask() {
    NftCollectionConfig.sync()
}

const val NETWORK_NAME_MAINNET = "mainnet"
const val NETWORK_NAME_TESTNET = "testnet"
const val NETWORK_NAME_SANDBOX = "sandboxnet"