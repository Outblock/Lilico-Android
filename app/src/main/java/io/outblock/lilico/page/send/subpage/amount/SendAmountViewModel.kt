package io.outblock.lilico.page.send.subpage.amount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.BalanceCallback
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnWalletDataUpdate
import io.outblock.lilico.manager.account.WalletManager
import io.outblock.lilico.manager.coin.CoinMapManager
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.network.model.AddressInfoAccount
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.send.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.utils.Coin
import io.outblock.lilico.utils.extensions.toSafeLong
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope

class SendAmountViewModel : ViewModel(), OnWalletDataUpdate, BalanceCallback {
    private lateinit var contact: AddressBookContact

    val balanceLiveData = MutableLiveData<SendBalanceModel>()

    val onCoinSwap = MutableLiveData<Boolean>()

    private var currentCoin = Coin.FLOW
    private var convertCoin = Coin.USD

    private var coinList = Coin.values()

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

    override fun onBalanceUpdate(balance: AddressInfoAccount) {
        val coinRate = CoinRateManager.coinRate(CoinMapManager.getCoinIdByName("Flow")) { rate ->
            logd("onBalanceUpdate 2", rate.usdRate())
            balanceLiveData.postValue(
                SendBalanceModel(
                    address = balance.address,
                    balance = balance.balance.toSafeLong(),
                    coinRate = rate.usdRate()?.price ?: -1f,
                )
            )
        }
        logd("onBalanceUpdate 1", coinRate?.usdRate())
        balanceLiveData.postValue(
            SendBalanceModel(
                address = balance.address,
                balance = balance.balance.toSafeLong(),
                coinRate = coinRate?.usdRate()?.price ?: -1f,
            )
        )
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        ioScope {
            wallet.primaryWallet()?.blockchain?.firstOrNull()?.address?.let { BalanceManager.getBalanceByAddress(it) }
        }
    }
}

