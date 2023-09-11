package io.outblock.lilico.page.staking.providers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.R
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.page.staking.providers.model.ProviderTitleModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.ioScope

class StakeProviderViewModel : ViewModel() {

    val data = MutableLiveData<List<Any>>()

    fun load() {
        ioScope {
            val providers = StakingManager.providers()
            val lilico = providers.firstOrNull { it.name == "Lilico" }
            val list = mutableListOf<Any>().apply {
//                todo hide stake entrance for rebranding
//                if (lilico != null) {
//                    add(ProviderTitleModel(R.string.recommend.res2String()))
//                    add(lilico)
//                }
                add(ProviderTitleModel(R.string.staking_provider.res2String()))
                addAll(providers.filter { it != lilico })
            }
            data.postValue(list)
        }
    }
}