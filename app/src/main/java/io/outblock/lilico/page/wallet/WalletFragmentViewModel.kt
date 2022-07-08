package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.account.*
import io.outblock.lilico.manager.coin.*
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.flowscan.flowScanAccountTransferCountQuery
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.*
import java.util.concurrent.CopyOnWriteArrayList

class WalletFragmentViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate, OnCoinRateUpdate, TokenStateChangeListener {

    val dataListLiveData = MutableLiveData<List<WalletCoinItemModel>>()

    val headerLiveData = MutableLiveData<WalletHeaderModel?>()

    private val dataList = CopyOnWriteArrayList<WalletCoinItemModel>()

    init {
        WalletManager.addListener(this)
        TokenStateManager.addListener(this)
        CoinRateManager.addListener(this)
        BalanceManager.addListener(this)
    }

    fun load() {
        viewModelIOScope(this) {
            logd(TAG, "view model load")
            loadWallet()
            loadCoinList()
            loadTransactionCount()
        }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        headerLiveData.postValue(WalletHeaderModel(wallet, 0f))
        updateWalletHeader()
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
            uiScope {
                val newCoin = coinList.filter { coin -> dataList.firstOrNull { it.coin.symbol == coin.symbol } == null }
                if (newCoin.isNotEmpty()) {
                    dataList.addAll(newCoin.map { WalletCoinItemModel(it, it.address(), 0f, 0f, isHideBalance = isHideBalance) })
                    logd(TAG, "loadCoinList newCoin:${newCoin.map { it.symbol }}")
                    logd(TAG, "loadCoinList dataList:${dataList.map { it.coin.symbol }}")
                    dataListLiveData.postValue(dataList)
                    updateWalletHeader(count = coinList.size)
                }
            }

            BalanceManager.refresh()
            CoinRateManager.refresh()

            uiScope { coinList.toList().firstOrNull { it.symbol == "fusd" }?.let { updateCoinRate(it, forceRate = 1.0f) } }
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
        uiScope { headerLiveData.value = headerLiveData.value?.copy(transactionCount = count) }
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

    private fun updateWalletHeader(count: Int? = null) {
        val header = headerLiveData.value ?: return
        headerLiveData.postValue(header.copy().apply {
            balance = dataList.toList().map { it.balance * it.coinRate }.sum()
            if (count != null) {
                coinCount = count
            }
        })
    }

    companion object {
        private val TAG = WalletFragmentViewModel::class.java.simpleName
    }
}