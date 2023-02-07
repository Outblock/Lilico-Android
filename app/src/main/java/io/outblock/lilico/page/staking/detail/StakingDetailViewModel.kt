package io.outblock.lilico.page.staking.detail

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
import io.outblock.lilico.manager.flowjvm.*
import io.outblock.lilico.manager.staking.StakingManager
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.manager.staking.createStakingDelegatorId
import io.outblock.lilico.manager.staking.delegatorId
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.page.profile.subpage.currency.model.selectedCurrency
import io.outblock.lilico.page.staking.detail.model.StakingDetailModel
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay

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

    fun claimRewards(provider: StakingProvider) {
        CADENCE_CLAIM_REWARDS.rewardsAction(provider)
    }

    fun restakeRewards(provider: StakingProvider) {
        CADENCE_RESTAKE_REWARDS.rewardsAction(provider)
    }

    fun claimUnstaked(provider: StakingProvider) {
        CADENCE_STAKING_UNSATKED_CLAIM.rewardsAction(provider)
    }

    fun restakeUnstaked(provider: StakingProvider) {
        CADENCE_STAKING_UNSATKED_RESTAKE.rewardsAction(provider)
    }

    private fun String.rewardsAction(provider: StakingProvider, isUnStaked: Boolean = false) {
        ioScope {

            var delegatorId = provider.delegatorId()
            if (delegatorId == null) {
                createStakingDelegatorId(provider)
                delay(2000)
                StakingManager.refreshDelegatorInfo()
                delegatorId = provider.delegatorId()
            }
            if (delegatorId == null) {
                return@ioScope
            }

            val amount = if (isUnStaked) {
                StakingManager.stakingInfo().nodes.first { it.nodeID == provider.id }.tokensUnstaked
            } else StakingManager.stakingInfo().nodes.first { it.nodeID == provider.id }.tokensRewarded
            val txid = transactionByMainWallet {
                arg { string(provider.id) }
                arg { uint32(delegatorId) }
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