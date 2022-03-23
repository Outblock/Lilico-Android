package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.*
import io.outblock.lilico.manager.coin.CoinMapManager
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.network.model.CoinRate
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class WalletFragmentViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate {

    val dataListLiveData = MutableLiveData<List<Any>>()

    private var balanceList = ConcurrentHashMap<String, Float>()

    private val dataList = CopyOnWriteArrayList<Any>()

    init {
        WalletManager.addListener(this)
    }

    fun load() {
        viewModelIOScope(this) {
            logd(TAG, "view model load")
            WalletManager.fetch()
            loadCoinList()
        }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        if (dataList.isEmpty()) {
            dataList.add(WalletHeaderModel(wallet, 0f))
        } else {
            dataList[0] = WalletHeaderModel(wallet, 0f)
        }
        dataListLiveData.postValue(dataList)
        updateWalletHeader()
    }

    override fun onBalanceUpdate(balance: Balance) {
        logd(TAG, "onBalanceUpdate:$balance")
        if (balance.symbol.lowercase() == "fusd") {
            updateBalanceLine(balance, forceRate = 1.0f)
            return
        }
        CoinRateManager.coinRate(CoinMapManager.getCoinIdBySymbol(balance.symbol)) { rate -> updateBalanceLine(balance, rate) }
    }

    private fun loadCoinList() {
        viewModelIOScope(this) {
            val coinList = FlowCoinListManager.coinList().filter { TokenStateManager.isTokenAdded(it.address()) }

            val newCoin =
                coinList.filter { coin -> dataList.filterIsInstance<WalletCoinItemModel>().indexOfFirst { coin.symbol == it.coin.symbol } < 0 }
            if (newCoin.isNotEmpty()) {
                dataList.addAll(newCoin.map { WalletCoinItemModel(it, it.address(), 0f, 0f) })
                logd(TAG, "loadCoinList:${newCoin.map { it.symbol }}")
                dataListLiveData.postValue(dataList)
                updateWalletHeader(count = coinList.size)
            }

            BalanceManager.addListener(this)

            coinList.forEach { BalanceManager.getBalanceByCoin(it) }
        }
    }

    private fun updateBalanceLine(balance: Balance, coinRate: CoinRate? = null, forceRate: Float? = null) {
        logd(TAG, "updateBalanceLine :$balance")
        val existIndex = dataList.indexOfFirst { it is WalletCoinItemModel && it.coin.symbol == balance.symbol }
        val rate = (coinRate?.usdRate()?.price ?: forceRate) ?: 0f
        val coin = FlowCoinListManager.coinList().first { it.symbol == balance.symbol }
        val item = WalletCoinItemModel(coin, coin.address(), balance.balance, rate)
        if (existIndex >= 0) {
            dataList[existIndex] = item
        } else {
            dataList.add(item)
        }
        dataListLiveData.postValue(dataList)
        balanceList[balance.symbol] = balance.balance * rate
        updateWalletHeader()
    }

    private fun updateWalletHeader(count: Int? = null) {
        val header = (dataList.firstOrNull { it is WalletHeaderModel } as? WalletHeaderModel) ?: return
        dataList[0] = header.copy().apply {
            balance = balanceList.map { it.value }.sum()
            if (count != null) {
                coinCount = count
            }
        }
        dataListLiveData.postValue(dataList)
    }

    companion object {
        private val TAG = WalletFragmentViewModel::class.java.simpleName
    }
}