package io.outblock.lilico.manager.flowjvm.transaction.resolve

import io.outblock.lilico.manager.flowjvm.transaction.Interaction


interface Resolver {
    suspend fun resolve(ix: Interaction)
}