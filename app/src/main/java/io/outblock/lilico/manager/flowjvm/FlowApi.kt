package io.outblock.lilico.manager.flowjvm

import com.fasterxml.jackson.databind.DeserializationFeature
import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.FlowAccessApi
import com.nftco.flow.sdk.FlowChainId
import com.nftco.flow.sdk.impl.FlowAccessApiImpl
import io.outblock.lilico.manager.app.isSandboxNet
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.logd

internal object FlowApi {
    private const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
    private const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
    private const val HOST_CANARYNET = "access.canary.nodes.onflow.org"
    private const val HOST_SANDBOXNET = "access.sandboxnet.nodes.onflow.org"

    private var api: FlowAccessApi? = null

    init {
        Flow.OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    }

    fun refreshConfig() {
        logd("FlowApi", "refreshConfig start")
        logd("FlowApi", "chainId:${chainId()}")
        (api as? FlowAccessApiImpl)?.close()
        Flow.configureDefaults(
            chainId = chainId(),
            addressRegistry = FlowAddressRegistry().addressRegistry()
        )
        api = Flow.newAccessApi(host(), 9000)
        logd("FlowApi", "DEFAULT_CHAIN_ID:${Flow.DEFAULT_CHAIN_ID}")
        logd("FlowApi", "DEFAULT_ADDRESS_REGISTRY:${Flow.DEFAULT_ADDRESS_REGISTRY}")
        logd("FlowApi", "isTestnet():${isTestnet()}")
        logd("FlowApi", "refreshConfig end")
    }

    fun get(): FlowAccessApi {
        val chainId = if (isTestnet()) FlowChainId.TESTNET else FlowChainId.MAINNET
        if (Flow.DEFAULT_CHAIN_ID != chainId) {
            refreshConfig()
        }
        return api ?: Flow.newAccessApi(if (isTestnet()) HOST_TESTNET else HOST_MAINNET, 9000)
    }

    private fun chainId() = when {
        isTestnet() -> FlowChainId.TESTNET
        isSandboxNet() -> FlowChainId.SANDBOX
        else -> FlowChainId.MAINNET
    }

    private fun host() = when {
        isTestnet() -> HOST_TESTNET
        isSandboxNet() -> HOST_SANDBOXNET
        else -> HOST_MAINNET
    }
}