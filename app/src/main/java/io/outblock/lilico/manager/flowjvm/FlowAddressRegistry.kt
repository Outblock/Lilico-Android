package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.AddressRegistry
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowChainId
import io.outblock.lilico.utils.isDev

internal class FlowAddressRegistry {
    private val registry = AddressRegistry()

    init {
        register(if (isDev()) FlowChainId.TESTNET else FlowChainId.MAINNET)
    }

    private fun register(net: FlowChainId) {
        registry.register("0xFind", FlowAddress(if (isDev()) "0xa16ab1d0abde3625" else "0x097bafa4e0b48eef"), net)
        registry.register("0xDomains", FlowAddress(if (isDev()) "0xb05b2abb42335e88" else "0x233eb012d34b0070"), net)
        registry.register("0xFlowns", FlowAddress(if (isDev()) "0xb05b2abb42335e88" else "0x233eb012d34b0070"), net)
        registry.register("0xFlowToken", FlowAddress(if (isDev()) "0x7e60df042a9c0868" else "0x1654653399040a61"), net)
        registry.register("0xFungibleToken", FlowAddress(if (isDev()) "0x9a0766d93b6608b7" else "0xf233dcee88fe0abe"), net)
        registry.register("0xNonFungibleToken", FlowAddress(if (isDev()) "0x1d7e57aa55817448" else "0x631e88ae7f1d7c20"), net)
    }

    fun addressRegistry() = registry

}