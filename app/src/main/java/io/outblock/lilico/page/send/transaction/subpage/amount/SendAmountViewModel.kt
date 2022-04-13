package io.outblock.lilico.page.send.transaction.subpage.amount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.Balance
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.page.send.transaction.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.utils.COIN_USD_SYMBOL
import io.outblock.lilico.utils.viewModelIOScope

class SendAmountViewModel : ViewModel(), OnBalanceUpdate, OnCoinRateUpdate {
    private lateinit var contact: AddressBookContact

    val balanceLiveData = MutableLiveData<SendBalanceModel>()

    val onCoinSwap = MutableLiveData<Boolean>()

    private var currentCoin = FlowCoin.SYMBOL_FLOW
    private var convertCoin = COIN_USD_SYMBOL

    init {
        BalanceManager.addListener(this)
        CoinRateManager.addListener(this)
    }

    fun currentCoin() = currentCoin
    fun convertCoin() = convertCoin

    fun setContact(contact: AddressBookContact) {
        this.contact = contact
    }

    fun contact() = contact

    fun load() {
        viewModelIOScope(this) {
            val coin = FlowCoinListManager.getCoin(currentCoin) ?: return@viewModelIOScope
            balanceLiveData.postValue(SendBalanceModel(symbol = coin.symbol))
            BalanceManager.getBalanceByCoin(coin)
            CoinRateManager.fetchCoinRate(coin)
        }
    }

    fun swapCoin() {
        val temp = currentCoin
        currentCoin = convertCoin
        convertCoin = temp
        onCoinSwap.postValue(true)
    }

    fun changeCoin(coin: FlowCoin) {
        if (currentCoin == coin.symbol) {
            return
        }
        currentCoin = coin.symbol
        onCoinSwap.postValue(true)
        load()
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        if (coin.symbol != currentCoin) {
            return
        }
        val data = balanceLiveData.value ?: SendBalanceModel(coin.symbol)
        balanceLiveData.value = data.copy(balance = balance.balance)
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        if (coin.symbol != currentCoin) {
            return
        }
        val data = balanceLiveData.value ?: SendBalanceModel(coin.symbol)
        balanceLiveData.value = data.copy(coinRate = price)
    }
}

