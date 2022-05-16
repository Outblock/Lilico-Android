package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.AddressRegistry
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowChainId
import io.outblock.lilico.manager.app.isTestnet

internal class FlowAddressRegistry {

    private fun AddressRegistry.register(net: FlowChainId) {
        addressMap().forEach { (t, u) -> register(t, FlowAddress(u), net) }
    }

    fun addressRegistry() = AddressRegistry().apply {
        register(if (isTestnet()) FlowChainId.TESTNET else FlowChainId.MAINNET)
    }

    companion object {
        fun addressMap() = mapOf(
            "0xFind" to if (isTestnet()) "0xa16ab1d0abde3625" else "0x097bafa4e0b48eef",
            "0xDomains" to if (isTestnet()) "0xb05b2abb42335e88" else "0x233eb012d34b0070",
            "0xFlowns" to if (isTestnet()) "0xb05b2abb42335e88" else "0x233eb012d34b0070",
            "0xFlowToken" to if (isTestnet()) "0x7e60df042a9c0868" else "0x1654653399040a61",
            "0xFungibleToken" to if (isTestnet()) "0x9a0766d93b6608b7" else "0xf233dcee88fe0abe",
            "0xNonFungibleToken" to if (isTestnet()) "0x631e88ae7f1d7c20" else "0x1d7e57aa55817448",
        )
    }
}

internal fun String.replaceFlowAddress(): String {
    var str = this
    FlowAddressRegistry.addressMap().forEach { (name, address) -> str = str.replace(name, address) }
    return str
}