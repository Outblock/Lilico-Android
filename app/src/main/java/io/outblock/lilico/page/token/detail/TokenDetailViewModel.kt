package io.outblock.lilico.page.token.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.network.ApiCryptowatchService
import io.outblock.lilico.network.cryptoWatchRetrofit
import io.outblock.lilico.network.model.CoinRate
import io.outblock.lilico.utils.getQuoteMarket
import io.outblock.lilico.utils.viewModelIOScope

class TokenDetailViewModel : ViewModel(), OnBalanceUpdate, OnCoinRateUpdate {

    private lateinit var coin: FlowCoin

    val balanceAmountLiveData = MutableLiveData<Float>()
    val balancePriceLiveData = MutableLiveData<Float>()

    val charDataLiveData = MutableLiveData<List<Quote>>()

    private var coinRate = 0.0f

    private var period: Period? = null
    private var market: String? = null

    init {
        BalanceManager.addListener(this)
        CoinRateManager.addListener(this)
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        balanceAmountLiveData.postValue(balance.balance)
        balancePriceLiveData.postValue(coinRate * balance.balance)
    }

    override fun onCoinRateUpdate(coin: FlowCoin, rate: CoinRate) {
        coinRate = rate.usdRate()?.price ?: 0f
        balancePriceLiveData.postValue(coinRate * (balanceAmountLiveData.value ?: 0f))
    }

    fun setCoin(coin: FlowCoin) {
        this.coin = coin
    }

    fun load() {
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
    }

    fun changePeriod(period: Period) {
        this.period = period
        refreshCharData(period)
    }

    private fun refreshCharData(period: Period) {
        viewModelIOScope(this) {
            val market = getQuoteMarket()
            this.market = market
            val service = cryptoWatchRetrofit().create(ApiCryptowatchService::class.java)
            val result = service.ohlc(
                market = market,
                coinPair = coin.getPricePair(QuoteMarket.fromMarketName(market)),
                before = if (period == Period.ALL) System.currentTimeMillis() / 1000 else null,
                after = if (period == Period.ALL) null else period.getChartPeriodTs(),
                periods = intArrayOf(period.getChartPeriodFrequency()),
            )
            val data = result.parseMarketQuoteData(period)
            if (this.period == period && this.market == market) {
                charDataLiveData.postValue(data)
            }
        }
    }
}