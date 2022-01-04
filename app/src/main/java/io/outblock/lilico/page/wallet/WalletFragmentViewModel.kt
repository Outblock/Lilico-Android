package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.managet.WalletListFetcher
import io.outblock.lilico.network.model.WalletListData
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.wallet.model.WalletCoinItemModel
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.page.wallet.model.WalletHeaderPlaceholderModel
import io.outblock.lilico.utils.extensions.toSafeLong
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.toAddress
import wallet.core.jni.CoinType

class WalletFragmentViewModel : ViewModel() {

    val dataListLiveData = MutableLiveData<List<Any>>()

    private val walletListFetcher by lazy {
        WalletListFetcher { walletList, isFromCache ->
            val data = dataListLiveData.value.orEmpty().toMutableList()
            if (data.isEmpty()) {
                data.add(WalletHeaderModel(walletList))
            } else {
                data[0] = WalletHeaderModel(walletList)
            }
            dataListLiveData.postValue(data)
            if (!isFromCache) {
                loadAddress(walletList)
            }
        }
    }

    fun load() {
        viewModelIOScope(this) {
            if (!walletListFetcher.cacheExist()) {
                val data = mutableListOf<Any>()
                data.add(WalletHeaderPlaceholderModel())
                dataListLiveData.postValue(data)
            }
            walletListFetcher.fetch()
        }
    }

    override fun onCleared() {
        walletListFetcher.stop()
        super.onCleared()
    }

    private fun loadAddress(walletData: WalletListData) {
        viewModelIOScope(this) {
            val blockchainList = walletData.primaryWallet()?.blockchain ?: return@viewModelIOScope

            val service = retrofit().create(ApiService::class.java)
            for (blockchain in blockchainList) {
                val resp = service.getAddressInfo(blockchain.address.toAddress())
                val data = dataListLiveData.value.orEmpty().toMutableList()
                data.add(WalletCoinItemModel(CoinType.FLOW, blockchain.address, resp.data.data.account.balance.toSafeLong()))
                dataListLiveData.postValue(data)
            }
        }
    }


}