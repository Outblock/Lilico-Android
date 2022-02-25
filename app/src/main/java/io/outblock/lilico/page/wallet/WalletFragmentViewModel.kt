package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.account.OnBalanceUpdate
import io.outblock.lilico.manager.account.OnWalletDataUpdate
import io.outblock.lilico.manager.account.WalletManager
import io.outblock.lilico.manager.coin.CoinMapManager
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.network.model.AddressInfoAccount
import io.outblock.lilico.network.model.CoinRate
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.utils.Coin
import io.outblock.lilico.utils.extensions.toSafeLong
import io.outblock.lilico.utils.formatBalance
import io.outblock.lilico.utils.name
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.toAddress
import wallet.core.jni.CoinType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class WalletFragmentViewModel : ViewModel(), OnWalletDataUpdate, OnBalanceUpdate {

    val dataListLiveData = MutableLiveData<List<Any>>()

    private var balanceList = ConcurrentHashMap<String, Float>()

    private val dataList = CopyOnWriteArrayList<Any>()

    init {
        WalletManager.addListener(this)
        BalanceManager.addListener(this)
    }

    fun load() {
        viewModelIOScope(this) { WalletManager.fetch() }
    }

    private fun loadAddress(walletData: WalletListData) {
        viewModelIOScope(this) {
            val blockchainList = walletData.primaryWallet()?.blockchain ?: return@viewModelIOScope

            for (blockchain in blockchainList) {
                BalanceManager.getBalanceByAddress(blockchain.address.toAddress())
            }
        }
    }

    override fun onWalletDataUpdate(wallet: WalletListData) {
        if (dataList.isEmpty()) {
            dataList.add(WalletHeaderModel(wallet, 0f))
        } else {
            dataList[0] = WalletHeaderModel(wallet, 0f)
        }
        dataListLiveData.postValue(dataList)
        loadAddress(wallet)
    }

    override fun onBalanceUpdate(balance: AddressInfoAccount) {
        // TODO get coin type by address
        // val wallet = (dataListLiveData.value?.first() as? WalletListData) ?: return
        // val coinType = wallet.primaryWallet()?.blockchain?.firstOrNull { it.address.toAddress() == balance.address.toAddress() }
        val coinType = Coin.FLOW.name()
        CoinRateManager.coinRate(CoinMapManager.getCoinIdByName(coinType)) { rate -> updateBalanceLine(balance, rate) }
    }

    private fun updateBalanceLine(balance: AddressInfoAccount, coinRate: CoinRate) {
        val existIndex = dataList.indexOfFirst { it is WalletCoinItemModel && it.address.toAddress() == balance.address.toAddress() }
        val rate = coinRate.usdRate()?.price ?: 0f
        if (existIndex >= 0) {
            dataList[existIndex] = WalletCoinItemModel(CoinType.FLOW, balance.address, balance.balance.toSafeLong(), rate)
        } else {
            dataList.add(WalletCoinItemModel(CoinType.FLOW, balance.address, balance.balance.toSafeLong(), rate))
        }
        dataListLiveData.postValue(dataList)
        balanceList[balance.address] = balance.balance.toSafeLong().formatBalance() * rate
        updateWalletBalance()
    }

    private fun updateWalletBalance() {
        val header = (dataList.firstOrNull { it is WalletHeaderModel } as? WalletHeaderModel) ?: return
        header.balance = balanceList.map { it.value }.sum()
        dataList[0] = header
        dataListLiveData.postValue(dataList)
    }
}