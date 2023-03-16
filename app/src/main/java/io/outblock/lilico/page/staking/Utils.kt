package io.outblock.lilico.page.staking

import android.content.Context
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.page.staking.detail.StakingDetailActivity
import io.outblock.lilico.page.staking.guide.StakeGuideActivity
import io.outblock.lilico.page.staking.list.StakingListActivity
import io.outblock.lilico.page.staking.providers.StakingProviderActivity


fun openStakingPage(context: Context) {
    if (StakingManager.isStaked()) {
        if (StakingManager.stakingInfo().nodes.isEmpty()) {
            StakingProviderActivity.launch(context)
        } else if (StakingManager.stakingInfo().nodes.size == 1) {
            val provider = StakingManager.providers().firstOrNull { it.id == StakingManager.stakingInfo().nodes.first().nodeID } ?: return
            StakingDetailActivity.launch(context, provider)
        } else StakingListActivity.launch(context)
    } else StakeGuideActivity.launch(context)
}

