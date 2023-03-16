package io.outblock.lilico.page.staking.amount

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
import io.outblock.lilico.utils.ioScope

class StakingAmountViewModel : ViewModel(), OnBalanceUpdate, OnCoinRateUpdate {

    val balanceLiveData = MutableLiveData<Float>()

    val processingLiveData = MutableLiveData<Boolean>()

    private var coinRate = 0.0f

    init {
        BalanceManager.addListener(this)
        CoinRateManager.addListener(this)
    }


    fun coinRate() = coinRate

    fun load(provider: StakingProvider, isUnstake: Boolean) {
        ioScope {
            val coin = FlowCoinListManager.getCoin(FlowCoin.SYMBOL_FLOW) ?: return@ioScope
            if (isUnstake) {
                val node = StakingManager.stakingNode(provider) ?: return@ioScope
                balanceLiveData.postValue(node.tokensStaked)
            } else {
                BalanceManager.getBalanceByCoin(coin)
            }
            CoinRateManager.fetchCoinRate(coin)
        }
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        if (coin.isFlowCoin()) {
            balanceLiveData.postValue(balance.balance)
        }
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        coinRate = price
    }
}