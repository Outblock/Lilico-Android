package io.outblock.lilico.page.staking.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.page.staking.list.model.StakingListItemModel
import io.outblock.lilico.utils.ioScope

class StakeListViewModel : ViewModel() {

    val data = MutableLiveData<List<StakingListItemModel>>()

    fun load() {
        ioScope {
            data.postValue(StakingManager.stakingInfo().nodes.map { node ->
                StakingListItemModel(
                    provider = StakingManager.providers().first { it.id == node.nodeID },
                    stakingNode = node,
                )
            })
        }
    }
}