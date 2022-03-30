package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.FlowChainId
import io.outblock.lilico.manager.app.isTestnet

internal object FlowApi {
    private const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
    private const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
    private const val HOST_CANARYNET = "access.canary.nodes.onflow.org"

    private val api by lazy { Flow.newAccessApi(if (isTestnet()) HOST_TESTNET else HOST_MAINNET, 9000) }

    init {
        refreshConfig()
    }

    fun refreshConfig() {
        Flow.configureDefaults(
            chainId = if (isTestnet()) FlowChainId.TESTNET else FlowChainId.MAINNET,
            addressRegistry = FlowAddressRegistry().addressRegistry()
        )
    }

    fun get() = api
}