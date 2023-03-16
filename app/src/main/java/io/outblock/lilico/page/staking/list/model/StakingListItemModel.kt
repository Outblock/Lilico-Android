package io.outblock.lilico.page.staking.list.model

import io.outblock.lilico.manager.staking.StakingNode
import io.outblock.lilico.manager.staking.StakingProvider

data class StakingListItemModel(
    val provider: StakingProvider,
    val stakingNode: StakingNode,
)