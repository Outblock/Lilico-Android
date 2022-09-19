package io.outblock.lilico.page.swap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.flowscan.contractId
import io.outblock.lilico.network.retrofitWithHost
import io.outblock.lilico.utils.viewModelIOScope

class SwapViewModel : ViewModel(), OnBalanceUpdate, OnCoinRateUpdate {

    val fromCoinLiveData = MutableLiveData<FlowCoin>()
    val toCoinLiveData = MutableLiveData<FlowCoin>()
    val onBalanceUpdate = MutableLiveData<Boolean>()
    val onCoinRateUpdate = MutableLiveData<Boolean>()
    val onEstimateFromUpdate = MutableLiveData<Float>()
    val onEstimateToUpdate = MutableLiveData<Float>()

    val onEstimateLoading = MutableLiveData<Boolean>()

    private val balanceMap: MutableMap<String, Balance> = mutableMapOf()
    private val coinRateMap: MutableMap<String, Float> = mutableMapOf()

    private var fromAmount = 0.0f
    private var toAmount = 0.0f

    init {
        val coin = FlowCoinListManager.getCoin(FlowCoin.SYMBOL_FLOW)!!
        fromCoinLiveData.value = coin
        BalanceManager.addListener(this)
        CoinRateManager.addListener(this)
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
    }

    fun fromCoinBalance() = if (fromCoin() == null) 0.0f else balanceMap[fromCoin()!!.symbol]?.balance ?: 0.0f
    fun toCoinBalance() = if (toCoin() == null) 0.0f else balanceMap[toCoin()!!.symbol]?.balance ?: 0.0f

    fun fromCoin() = fromCoinLiveData.value
    fun toCoin() = toCoinLiveData.value

    fun fromCoinRate(): Float = coinRateMap[fromCoin()!!.symbol] ?: 0.0f

    fun updateFromCoin(coin: FlowCoin) {
        fromCoinLiveData.value = coin
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
    }

    fun updateToCoin(coin: FlowCoin) {
        toCoinLiveData.value = coin
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
    }

    fun updateFromAmount(amount: Float) {
        if (fromAmount == amount) return
        fromAmount = amount
        requestEstimate(true)
    }

    fun updateToAmount(amount: Float) {
        if (toAmount == amount) return
        toAmount = amount
        requestEstimate(false)
    }

    fun switchCoin() {
        val fromCoin = fromCoin()
        val toCoin = toCoin()
        if (fromCoin == null || toCoin == null) {
            return
        }

        updateFromCoin(toCoin)
        updateToCoin(fromCoin)
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        balanceMap[coin.symbol] = balance
        onBalanceUpdate.value = true
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        coinRateMap[coin.symbol] = price
        onCoinRateUpdate.value = true
    }

    private fun requestEstimate(isFrom: Boolean) {
        if (fromCoin() == null || toCoin() == null) {
            return
        }
        onEstimateLoading.value = true
        viewModelIOScope(this) {
            val response = retrofitWithHost("https://lilico.app").create(ApiService::class.java).getSwapEstimate(
                inToken = fromCoin()!!.contractId(),
                outToken = toCoin()!!.contractId(),
                inAmount = if (isFrom) fromAmount else null,
                outAmount = if (isFrom) null else toAmount,
            )
            val data = response.data ?: return@viewModelIOScope
            val matched = if (isFrom) data.tokenInAmount == fromAmount else data.tokenOutAmount == toAmount
            if (matched) {
                if (isFrom) onEstimateToUpdate.postValue(data.tokenOutAmount) else onEstimateFromUpdate.postValue(data.tokenInAmount)
                onEstimateLoading.postValue(false)
            }
        }
    }
}