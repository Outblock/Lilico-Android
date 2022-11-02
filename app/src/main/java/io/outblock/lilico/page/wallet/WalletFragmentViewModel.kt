package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.account.*
import io.outblock.lilico.manager.coin.*
import io.outblock.lilico.manager.price.CurrencyManager
import io.outblock.lilico.manager.price.CurrencyUpdateListener
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.flowscan.flowScanAccountTransferCountQuery
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.profile.subpage.currency.model.selectedCurrency
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.*
import java.util.concurrent.CopyOnWriteArrayList

class WalletFragmentViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate, OnCoinRateUpdate, TokenStateChangeListener, CurrencyUpdateListener {

    val dataListLiveData = MutableLiveData<List<WalletCoinItemModel>>()

    val headerLiveData = MutableLiveData<WalletHeaderModel?>()

    private val dataList = CopyOnWriteArrayList<WalletCoinItemModel>()

    init {
        WalletManager.addListener(this)
        TokenStateManager.addListener(this)
        CoinRateManager.addListener(this)
        BalanceManager.addListener(this)
        CurrencyManager.addCurrencyUpdateListener(this)
    }

    fun load() {
        viewModelIOScope(this) {
            logd(TAG, "view model load")
            loadWallet()
            loadCoinList()
            loadTransactionCount()
            CurrencyManager.fetch()
        }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        updateWalletHeader(wallet = wallet)
        if (dataList.isEmpty()) {
            loadCoinList()
        }
        TokenStateManager.fetchState()
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        logd(TAG, "onBalanceUpdate:$balance")
        updateCoinBalance(balance)
    }

    override fun onTokenStateChange(coin: FlowCoin, isEnable: Boolean) {
        loadCoinList()
        viewModelIOScope(this) { loadTransactionCount() }
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        updateCoinRate(coin, price)
    }

    override fun onCurrencyUpdate(flag: String, price: Float) {
        ioScope {
            val currency = selectedCurrency()
            dataList.forEachIndexed { index, item -> dataList[index] = item.copy(currency = currency.flag) }
            dataListLiveData.postValue(dataList)
            loadCoinList()
        }
    }

    fun onBalanceHideStateUpdate() {
        viewModelIOScope(this) {
            val isHideBalance = isHideWalletBalance()
            val data = dataList.toList().map { it.copy(isHideBalance = isHideBalance) }

            dataListLiveData.postValue(data)
        }
    }

    private suspend fun loadWallet() {
        if (!walletCache().isCacheExist()) {
            headerLiveData.postValue(null)
        }
        WalletManager.fetch()
    }

    private fun loadCoinList() {
        viewModelIOScope(this) {
            val coinList = FlowCoinListManager.coinList().filter { TokenStateManager.isTokenAdded(it.address()) }

            val isHideBalance = isHideWalletBalance()
            val currency = getCurrencyFlag()
            uiScope {
                val newCoin = coinList.filter { coin -> dataList.firstOrNull { it.coin.symbol == coin.symbol } == null }
                if (newCoin.isNotEmpty()) {
                    dataList.addAll(newCoin.map { WalletCoinItemModel(it, it.address(), 0f, 0f, isHideBalance = isHideBalance, currency = currency) })
                    logd(TAG, "loadCoinList newCoin:${newCoin.map { it.symbol }}")
                    logd(TAG, "loadCoinList dataList:${dataList.map { it.coin.symbol }}")
                    dataListLiveData.postValue(dataList)
                    updateWalletHeader(count = coinList.size)
                }
            }

            BalanceManager.refresh()
            CoinRateManager.refresh()
        }
    }

    private suspend fun loadTransactionCount() {
        val count = flowScanAccountTransferCountQuery() + TransactionStateManager.getProcessingTransaction().size
        val localCount = getAccountTransactionCountLocal()
        if (count < localCount) {
            logd(TAG, "loadTransactionCount remote count < local count:$count < $localCount")
            return
        }
        updateAccountTransactionCountLocal(count)
        updateWalletHeader()
    }

    private fun updateCoinBalance(balance: Balance) {
        logd(TAG, "updateCoinBalance :$balance")
        val oldItem = dataList.firstOrNull { it.coin.symbol == balance.symbol } ?: return
        val item = oldItem.copy(balance = balance.balance)
        dataList[dataList.indexOf(oldItem)] = item
        dataListLiveData.value = dataList
        updateWalletHeader()
    }

    private fun updateCoinRate(coin: FlowCoin, price: Float? = null, forceRate: Float? = null) {
        val rate = (price ?: forceRate) ?: 0f
        logd(TAG, "updateCoinRate ${coin.symbol}:$rate")

        val oldItem = dataList.firstOrNull { it.coin.symbol == coin.symbol } ?: return
        val item = oldItem.copy(coinRate = rate)
        dataList[dataList.indexOf(oldItem)] = item
        dataListLiveData.value = dataList
        updateWalletHeader()
    }

    private fun updateWalletHeader(wallet: WalletListData? = null, count: Int? = null) {
        uiScope {
            val header = headerLiveData.value ?: (if (wallet == null) return@uiScope else WalletHeaderModel(wallet, 0f))
            headerLiveData.postValue(header.copy().apply {
                balance = dataList.toList().map { it.balance * it.coinRate }.sum()
                count?.let { coinCount = it }
                transactionCount = getAccountTransactionCountLocal()
            })
        }
    }

    companion object {
        private val TAG = WalletFragmentViewModel::class.java.simpleName
    }

}