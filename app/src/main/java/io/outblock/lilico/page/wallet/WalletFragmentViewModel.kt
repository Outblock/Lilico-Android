package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.*
import io.outblock.lilico.manager.coin.*
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.isHideWalletBalance
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.viewModelIOScope
import java.util.concurrent.CopyOnWriteArrayList

class WalletFragmentViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate, OnCoinRateUpdate, TokenStateChangeListener {

    val dataListLiveData = MutableLiveData<List<WalletCoinItemModel>>()

    val headerLiveData = MutableLiveData<WalletHeaderModel>()

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
            WalletManager.fetch()
            loadCoinList()
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

    private fun loadCoinList() {
        viewModelIOScope(this) {
            val coinList = FlowCoinListManager.coinList().filter { TokenStateManager.isTokenAdded(it.address()) }

            val newCoin = coinList.filter { coin -> dataList.firstOrNull { it.coin.symbol == coin.symbol } == null }
            if (newCoin.isNotEmpty()) {
                val isHideBalance = isHideWalletBalance()
                dataList.addAll(newCoin.map { WalletCoinItemModel(it, it.address(), 0f, 0f, isHideBalance = isHideBalance) })
                logd(TAG, "loadCoinList newCoin:${newCoin.map { it.symbol }}")
                logd(TAG, "loadCoinList dataList:${dataList.map { it.coin.symbol }}")
                dataListLiveData.postValue(dataList)
                updateWalletHeader(count = coinList.size)
            }

            BalanceManager.refresh()
            CoinRateManager.refresh()

            uiScope { coinList.toList().firstOrNull { it.symbol == "fusd" }?.let { updateCoinRate(it, forceRate = 1.0f) } }
        }
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