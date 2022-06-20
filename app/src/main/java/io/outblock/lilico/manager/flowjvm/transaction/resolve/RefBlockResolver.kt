package io.outblock.lilico.manager.flowjvm.transaction.resolve

import io.outblock.fcl.resolve.Resolver
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.flowjvm.transaction.Interaction

class RefBlockResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        val block = FlowApi.get().getLatestBlock(sealed = true)
        ix.message.refBlock = block.id.base16Value
    }
}