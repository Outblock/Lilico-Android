package io.outblock.lilico.page.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.managet.WalletListFetcher
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.wallet.model.WalletHeaderModel
import io.outblock.lilico.page.wallet.model.WalletHeaderPlaceholderModel
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.toAddress

class WalletFragmentViewModel : ViewModel() {

    val dataListLiveData = MutableLiveData<List<Any>>()

    private val walletListFetcher by lazy {
        WalletListFetcher {
            val data = dataListLiveData.value.orEmpty().toMutableList()
            if (data.isEmpty()) {
                data.add(WalletHeaderModel(it))
            } else {
                data[0] = WalletHeaderModel(it)
            }
            dataListLiveData.postValue(data)

            ioScope {
                val service = retrofit().create(ApiService::class.java)
                val resp = service.getAddressInfo(it.primaryWallet()!!.blockchain.first().address.toAddress())
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


}