package io.outblock.lilico.manager.staking

object StakingManager {
    private val providers = StakingProviders().apply { refresh() }

    fun providers() = providers.get()
}

fun isStaked() = true