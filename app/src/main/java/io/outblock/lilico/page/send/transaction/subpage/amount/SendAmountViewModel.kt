package io.outblock.lilico.page.send.transaction.subpage.amount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.*
import io.outblock.lilico.manager.coin.CoinMapManager
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.send.transaction.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.utils.Coin
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope

class SendAmountViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate {
    private lateinit var contact: AddressBookContact

    val balanceLiveData = MutableLiveData<SendBalanceModel>()

    val onCoinSwap = MutableLiveData<Boolean>()

    private var currentCoin = Coin.FLOW
    private var convertCoin = Coin.USD

    init {
        WalletManager.addListener(this)
        BalanceManager.addListener(this)
    }

    fun currentCoin() = currentCoin
    fun convertCoin() = convertCoin

    fun setContact(contact: AddressBookContact) {
        this.contact = contact
    }

    fun contact() = contact

    fun load() {
        viewModelIOScope(this) { WalletManager.fetch() }
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

    override fun onBalanceUpdate(balance: Balance) {
        if (balance.symbol.lowercase() != "flow") {
            return
        }
        CoinRateManager.coinRate(CoinMapManager.getCoinIdBySymbol(balance.symbol)) { rate ->
            logd("onBalanceUpdate", rate.usdRate())
            balanceLiveData.postValue(
                SendBalanceModel(
                    balance = balance.balance,
                    coinRate = rate.usdRate()?.price ?: -1f,
                )
            )
        }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        ioScope {
            FlowCoinListManager.getEnabledCoinList().forEach { BalanceManager.getBalanceByCoin(it) }
        }
    }
}

