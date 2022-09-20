package io.outblock.lilico.page.swap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.app.chainNetWorkString
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.flowscan.contractId
import io.outblock.lilico.network.model.SwapEstimateResponse
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
    val estimateLiveData = MutableLiveData<SwapEstimateResponse.Data>()

    val onSwapTransactionSent = MutableLiveData<Boolean>()

    private val balanceMap: MutableMap<String, Balance> = mutableMapOf()
    private val coinRateMap: MutableMap<String, Float> = mutableMapOf()

    private var exactToken = ExactToken.FROM

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
        if (fromCoin() == coin) return
        fromCoinLiveData.value = coin
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
        requestEstimate()
    }

    fun updateToCoin(coin: FlowCoin) {
        if (toCoin() == coin) return
        toCoinLiveData.value = coin
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
        requestEstimate()
    }

    fun updateExactToken(exactToken: ExactToken) {
        this.exactToken = exactToken
        requestEstimate()
    }

    fun switchCoin() {
        val fromCoin = fromCoin()
        val toCoin = toCoin()
        if (fromCoin == null || toCoin == null) {
            return
        }

        exactToken = if (exactToken == ExactToken.FROM) ExactToken.TO else ExactToken.FROM

        updateFromCoin(toCoin)
        updateToCoin(fromCoin)
    }

    fun swap() {

    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        balanceMap[coin.symbol] = balance
        onBalanceUpdate.value = true
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        coinRateMap[coin.symbol] = price
        onCoinRateUpdate.value = true
    }

    private fun requestEstimate() {
        if (fromCoin() == null || toCoin() == null) return
        val binding = swapPageBinding() ?: return
        if (binding.fromAmount() == 0.0f && binding.toAmount() == 0.0f) return

        onEstimateLoading.value = true
        viewModelIOScope(this) {
            val response = kotlin.runCatching {
                retrofitWithHost("https://lilico.app").create(ApiService::class.java).getSwapEstimate(
                    network = chainNetWorkString(),
                    inToken = fromCoin()!!.contractId(),
                    outToken = toCoin()!!.contractId(),
                    inAmount = if (exactToken == ExactToken.FROM) binding.fromAmount() else null,
                    outAmount = if (exactToken == ExactToken.TO) binding.toAmount() else null,
                )
            }.getOrNull()!!
            val data = response.data
            val matched = if (exactToken == ExactToken.FROM) data.tokenInAmount == binding.fromAmount() else data.tokenOutAmount == binding.toAmount()
            if (matched) {
                if (exactToken == ExactToken.FROM) onEstimateToUpdate.postValue(data.tokenOutAmount) else onEstimateFromUpdate.postValue(data.tokenInAmount)
                onEstimateLoading.postValue(false)
                estimateLiveData.postValue(data)
            }
        }
    }
}

enum class ExactToken {
    FROM,
    TO
}