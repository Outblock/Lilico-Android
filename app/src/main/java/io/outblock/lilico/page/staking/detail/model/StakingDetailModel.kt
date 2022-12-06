package io.outblock.lilico.page.staking.detail.model

import io.outblock.lilico.manager.staking.StakingNode
import io.outblock.lilico.page.profile.subpage.currency.model.Currency

data class StakingDetailModel(
    var currency: Currency = Currency.USD,
    var balance: Float = 0.0f,
    var coinRate: Float = 0.0f,
    var stakingNode: StakingNode = StakingNode(),
)