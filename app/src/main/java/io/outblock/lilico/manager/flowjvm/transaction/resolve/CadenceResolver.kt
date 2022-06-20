package io.outblock.lilico.manager.flowjvm.transaction.resolve

import io.outblock.lilico.manager.flowjvm.replaceFlowAddress
import io.outblock.lilico.manager.flowjvm.transaction.Interaction

class CadenceResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        val cadence = ix.message.cadence ?: return

        ix.message.cadence = cadence.replaceFlowAddress()
    }
}