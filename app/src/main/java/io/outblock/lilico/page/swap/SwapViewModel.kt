package io.outblock.lilico.page.swap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager

class SwapViewModel : ViewModel() {

    val fromCoinLiveData = MutableLiveData<FlowCoin>()
    val toCoinLiveData = MutableLiveData<FlowCoin>()

    init {
        fromCoinLiveData.value = FlowCoinListManager.getCoin(FlowCoin.SYMBOL_FLOW)
    }


}