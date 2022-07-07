package io.outblock.lilico.page.token.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.flowscan.flowScanTokenTransferQuery
import io.outblock.lilico.network.model.CryptowatchSummaryData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.transaction.record.model.TransactionRecord
import io.outblock.lilico.utils.getQuoteMarket
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.updateQuoteMarket
import io.outblock.lilico.utils.viewModelIOScope
import java.util.concurrent.ConcurrentHashMap

class TokenDetailViewModel : ViewModel(), OnBalanceUpdate, OnCoinRateUpdate {

    private lateinit var coin: FlowCoin

    val balanceAmountLiveData = MutableLiveData<Float>()
    val balancePriceLiveData = MutableLiveData<Float>()

    val chartDataLiveData = MutableLiveData<List<Quote>>()
    val chartLoadingLiveData = MutableLiveData<Boolean>()
    val summaryLiveData = MutableLiveData<CryptowatchSummaryData.Result>()
    val transactionListLiveData = MutableLiveData<List<TransactionRecord>>()

    private var coinRate = 0.0f

    private var period: Period? = null
    private var market: String? = null

    private val chartCache = ConcurrentHashMap<String, List<Quote>>()

    init {
        BalanceManager.addListener(this)
        CoinRateManager.addListener(this)
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        balanceAmountLiveData.value = balance.balance
        balancePriceLiveData.value = coinRate * balance.balance
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        coinRate = price
        balancePriceLiveData.value = coinRate * (balanceAmountLiveData.value ?: 0f)
    }

    fun setCoin(coin: FlowCoin) {
        this.coin = coin
    }

    fun load() {
        BalanceManager.getBalanceByCoin(coin)
        CoinRateManager.fetchCoinRate(coin)
        viewModelIOScope(this) {
            flowScanTokenTransferQuery(coin)?.take(3)?.let { transactionListLiveData.postValue(it) }
        }
    }

    fun changePeriod(period: Period) {
        this.period = period
        refreshCharData(period)
        refreshSummary(period)
    }

    fun changeMarket(market: String) {
        ioScope {
            updateQuoteMarket(market)
            refreshCharData(this.period!!)
            refreshSummary(this.period!!)
        }
    }

    private fun refreshCharData(period: Period) {
        viewModelIOScope(this) {
            val market = getQuoteMarket()
            this.market = market

            if (chartCache[period.value + market] != null) {
                chartDataLiveData.postValue(chartCache[period.value + market])
                return@viewModelIOScope
            }

            chartLoadingLiveData.postValue(true)
            runCatching {
                val service = retrofit().create(ApiService::class.java)
                val result = service.ohlc(
                    market = market,
                    coinPair = coin.getPricePair(QuoteMarket.fromMarketName(market)),
                    after = if (period == Period.ALL) null else period.getChartPeriodTs(),
                    periods = "${period.getChartPeriodFrequency()}",
                )
                val data = result.parseMarketQuoteData(period)
                chartCache[period.value + market] = data
                if (this.period == period && this.market == market) {
                    chartDataLiveData.postValue(data)
                }
            }
            chartLoadingLiveData.postValue(false)
        }
    }

    private fun refreshSummary(period: Period) {
        viewModelIOScope(this) {
            val market = getQuoteMarket()
            val service = retrofit().create(ApiService::class.java)
            val result = service.summary(
                market = market,
                coinPair = coin.getPricePair(QuoteMarket.fromMarketName(market)),
            )
            if (this.period == period && this.market == market) {
                summaryLiveData.postValue(result.data.result)
            }
        }
    }
}