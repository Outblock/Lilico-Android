package io.outblock.lilico.page.staking.amount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.manager.flowjvm.CADENCE_STAKE_FLOW
import io.outblock.lilico.manager.flowjvm.transactionByMainWallet
import io.outblock.lilico.manager.flowjvm.ufix64Safe
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
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

    fun load() {
        ioScope {
            val coin = FlowCoinListManager.getCoin(FlowCoin.SYMBOL_FLOW) ?: return@ioScope
            BalanceManager.getBalanceByCoin(coin)
            CoinRateManager.fetchCoinRate(coin)
        }
    }

    fun stake(provider: StakingProvider, amount: Float) {
        ioScope {
            try {
                val txid = CADENCE_STAKE_FLOW.transactionByMainWallet {
                    arg { string(provider.id) }
                    arg { uint32(StakingManager.stakingInfo().delegatorId ?: 0) }
                    arg { ufix64Safe(amount) }
                }
                val transactionState = TransactionState(
                    transactionId = txid!!,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_TRANSACTION_DEFAULT,
                    data = ""
                )
                TransactionStateManager.newTransaction(transactionState)
                pushBubbleStack(transactionState)
                processingLiveData.postValue(true)
            } catch (e: Exception) {
                processingLiveData.postValue(false)
            }
        }
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        if (coin.symbol == FlowCoin.SYMBOL_FLOW) {
            balanceLiveData.postValue(balance.balance)
        }
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        coinRate = price
    }
}