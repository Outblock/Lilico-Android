package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.AddressRegistry
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowChainId
import io.outblock.lilico.utils.NETWORK_MAINNET
import io.outblock.lilico.utils.NETWORK_SANDBOX
import io.outblock.lilico.utils.NETWORK_TESTNET
import io.outblock.lilico.utils.logw

internal class FlowAddressRegistry {

    private fun AddressRegistry.register(network: Int) {
        addressMap(network).forEach { (t, u) ->
            logw("FlowAddressRegistry", "register  name:$t,address:$u,network:${network}")
            register(
                t, FlowAddress(u), when (network) {
                    NETWORK_MAINNET -> FlowChainId.MAINNET
                    NETWORK_TESTNET -> FlowChainId.TESTNET
                    NETWORK_SANDBOX -> FlowChainId.SANDBOX
                    else -> FlowChainId.MAINNET
                }
            )
        }
    }

    fun addressRegistry() = AddressRegistry().apply {
        register(NETWORK_MAINNET)
        register(NETWORK_TESTNET)
        register(NETWORK_SANDBOX)
    }

    companion object {
        fun addressMap(network: Int): Map<String, String> {
            val isTestNet = network == NETWORK_TESTNET
            return mapOf(
                "0xFind" to when (network) {
                    NETWORK_TESTNET -> "0xa16ab1d0abde3625"
                    else -> "0x097bafa4e0b48eef"
                },
                "0xDomains" to when (network) {
                    NETWORK_TESTNET -> "0xb05b2abb42335e88"
                    NETWORK_SANDBOX -> "0x8998b29311d1f3da"
                    else -> "0x233eb012d34b0070"
                },
                "0xFlowns" to when (network) {
                    NETWORK_TESTNET -> "0xb05b2abb42335e88"
                    NETWORK_SANDBOX -> "0x8998b29311d1f3da"
                    else -> "0x233eb012d34b0070"
                },
                "0xFlowToken" to when (network) {
                    NETWORK_TESTNET -> "0x7e60df042a9c0868"
                    NETWORK_SANDBOX -> "0x0661ab7d6696a460"
                    else -> "0x1654653399040a61"
                },
                "0xFungibleToken" to when (network) {
                    NETWORK_TESTNET -> "0x9a0766d93b6608b7"
                    NETWORK_SANDBOX -> "0xe20612a0776ca4bf"
                    else -> "0xf233dcee88fe0abe"
                },
                "0xNonFungibleToken" to when (network) {
                    NETWORK_TESTNET -> "0x631e88ae7f1d7c20"
                    NETWORK_SANDBOX -> "0x83ade3a54eb3870c"
                    else -> "0x1d7e57aa55817448"
                },
                "0xMetadataViews" to when (network) {
                    NETWORK_TESTNET -> "0x631e88ae7f1d7c20"
                    else -> "0x1d7e57aa55817448"
                },
                "0xStakingCollection" to when (network) {
                    NETWORK_TESTNET -> "0x95e019a17d0e23d7"
                    else -> "0x8d0e87b65159ae63"
                },
                "0xLockedTokens" to when (network) {
                    NETWORK_SANDBOX -> "0xf4527793ee68aede"
                    else -> "0x8d0e87b65159ae63"
                },
                "0xFlowIDTableStaking" to "0x8624b52f9ddcd04a",
                "0xFlowStakingCollection" to "0x8d0e87b65159ae63",
                "0xSwapRouter" to when (network) {
                    NETWORK_TESTNET -> "0x2f8af5ed05bbde0d"
                    else -> "0xa6850776a94e6551"
                },
                "0xSwapError" to when (network) {
                    NETWORK_TESTNET -> "0xddb929038d45d4b3"
                    else -> "0xb78ef7afa52ff906"
                },
            )
        }
    }
}