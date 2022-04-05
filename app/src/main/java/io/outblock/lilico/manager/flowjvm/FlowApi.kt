package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.FlowAccessApi
import com.nftco.flow.sdk.FlowChainId
import com.nftco.flow.sdk.impl.FlowAccessApiImpl
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.logd

internal object FlowApi {
    private const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
    private const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
    private const val HOST_CANARYNET = "access.canary.nodes.onflow.org"

    private var api: FlowAccessApi? = null

    init {
        refreshConfig()
    }

    fun refreshConfig() {
        logd("FlowApi", "refreshConfig start")
        (api as? FlowAccessApiImpl)?.close()
        Flow.configureDefaults(
            chainId = if (isTestnet()) FlowChainId.TESTNET else FlowChainId.MAINNET,
            addressRegistry = FlowAddressRegistry().addressRegistry()
        )
        api = Flow.newAccessApi(if (isTestnet()) HOST_TESTNET else HOST_MAINNET, 9000)
        logd("FlowApi", "isTestnet():${isTestnet()}")
        logd("FlowApi", "refreshConfig end")
    }

    fun get() = api ?: Flow.newAccessApi(if (isTestnet()) HOST_TESTNET else HOST_MAINNET, 9000)
}