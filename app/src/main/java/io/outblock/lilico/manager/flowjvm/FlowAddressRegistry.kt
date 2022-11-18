package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.AddressRegistry
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowChainId
import io.outblock.lilico.utils.logw

internal class FlowAddressRegistry {

    private fun AddressRegistry.register(isTestNet: Boolean) {
        addressMap(isTestNet).forEach { (t, u) ->
            logw("FlowAddressRegistry", "register  name:$t,address:$u,isTestNet:${isTestNet}")
            register(t, FlowAddress(u), if (isTestNet) FlowChainId.TESTNET else FlowChainId.MAINNET)
        }
    }

    fun addressRegistry() = AddressRegistry().apply {
        register(true)
        register(false)
    }

    companion object {
        fun addressMap(isTestNet: Boolean): Map<String, String> {
            return mapOf(
                "0xFind" to if (isTestNet) "0xa16ab1d0abde3625" else "0x097bafa4e0b48eef",
                "0xDomains" to if (isTestNet) "0xb05b2abb42335e88" else "0x233eb012d34b0070",
                "0xFlowns" to if (isTestNet) "0xb05b2abb42335e88" else "0x233eb012d34b0070",
                "0xFlowToken" to if (isTestNet) "0x7e60df042a9c0868" else "0x1654653399040a61",
                "0xFungibleToken" to if (isTestNet) "0x9a0766d93b6608b7" else "0xf233dcee88fe0abe",
                "0xNonFungibleToken" to if (isTestNet) "0x631e88ae7f1d7c20" else "0x1d7e57aa55817448",
                "0xMetadataViews" to if (isTestNet) "0x631e88ae7f1d7c20" else "0x1d7e57aa55817448",
                "0xStakingCollection" to if (isTestNet) "0x95e019a17d0e23d7" else "0x8d0e87b65159ae63",
                "0xLockedTokens" to "0x8d0e87b65159ae63",
                "0xFlowIDTableStaking" to "0x8624b52f9ddcd04a",
                "0xFlowStakingCollection" to "0x8d0e87b65159ae63",
            )
        }
    }
}