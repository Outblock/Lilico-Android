package io.outblock.lilico.page.staking.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.page.profile.subpage.currency.model.selectedCurrency
import io.outblock.lilico.page.staking.detail.model.StakingDetailModel
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope

class StakingDetailViewModel : ViewModel(), OnBalanceUpdate, OnCoinRateUpdate {

    val dataLiveData = MutableLiveData<StakingDetailModel>()

    init {
        BalanceManager.addListener(this)
        CoinRateManager.addListener(this)
    }


    fun load(provider: StakingProvider) {
        ioScope {
            updateLiveData(data().apply {
                currency = selectedCurrency()
                stakingNode = StakingManager.stakingInfo().nodes.first { it.nodeID == provider.id }
            })

            val coin = FlowCoinListManager.getCoin(FlowCoin.SYMBOL_FLOW) ?: return@ioScope

            logd("xxx", "coin:$coin")
            BalanceManager.getBalanceByCoin(coin)
            CoinRateManager.fetchCoinRate(coin)
        }
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        if (coin.isFlowCoin()) {
            logd("xxx", "balance:${balance.balance}")
            updateLiveData(data().apply { this.balance = balance.balance })
        }
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        logd("xxx", "price:${price}")
        if (coin.isFlowCoin()) {
            updateLiveData(data().apply { this.coinRate = price })
        }
    }

    private fun data() = (dataLiveData.value ?: StakingDetailModel()).copy()

    private fun updateLiveData(data: StakingDetailModel) {
        uiScope {
            logd("xxx", "updateLiveData:$data")
            dataLiveData.value = data
        }
    }
}