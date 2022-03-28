package io.outblock.lilico.page.send.transaction.subpage.amount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.*
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.CoinRate
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.send.transaction.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.utils.Coin
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.viewModelIOScope

class SendAmountViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate, OnCoinRateUpdate {
    private lateinit var contact: AddressBookContact

    val balanceLiveData = MutableLiveData<SendBalanceModel>()

    val onCoinSwap = MutableLiveData<Boolean>()

    private var currentCoin = Coin.FLOW
    private var convertCoin = Coin.USD

    init {
        WalletManager.addListener(this)
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
            WalletManager.fetch()
            val flow = FlowCoinListManager.coinList().first { it.isFlowCoin() }
            BalanceManager.getBalanceByCoin(flow)
            CoinRateManager.fetchCoinRate(flow)
        }
    }

    fun swapCoin() {
        val temp = currentCoin
        currentCoin = convertCoin
        convertCoin = temp
        onCoinSwap.postValue(true)
    }

    fun changeCoin(coin: Coin) {
        currentCoin = coin
        onCoinSwap.postValue(true)
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        ioScope {
            FlowCoinListManager.getEnabledCoinList().forEach { BalanceManager.getBalanceByCoin(it) }
        }
    }

    override fun onBalanceUpdate(coin: FlowCoin, balance: Balance) {
        if (!coin.isFlowCoin()) {
            return
        }
        val data = balanceLiveData.value ?: SendBalanceModel()
        balanceLiveData.postValue(data.copy(balance = balance.balance))
    }

    override fun onCoinRateUpdate(coin: FlowCoin, rate: CoinRate) {
        if (!coin.isFlowCoin()) {
            return
        }
        val data = balanceLiveData.value ?: SendBalanceModel()
        balanceLiveData.postValue(data.copy(coinRate = rate.usdRate()?.price ?: 0.0f))
    }
}

