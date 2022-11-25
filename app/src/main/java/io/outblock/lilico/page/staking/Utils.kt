package io.outblock.lilico.page.staking

import android.content.Context
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.page.staking.guide.StakeGuideActivity
import io.outblock.lilico.page.staking.list.StakingListActivity
import io.outblock.lilico.page.staking.providers.StakingProviderActivity


fun openStakingPage(context: Context) {
    if (StakingManager.isStaked()) {
        if (StakingManager.stakingInfo().nodes.isEmpty()) {
            StakingProviderActivity.launch(context)
        } else if (StakingManager.stakingInfo().nodes.size == 1) {
            // todo jump to staking detail page
            StakingListActivity.launch(context)
        } else StakingListActivity.launch(context)
    } else StakeGuideActivity.launch(context)
}