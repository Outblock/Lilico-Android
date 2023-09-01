package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope

class ChildAccountDetailViewModel : ViewModel() {
    val nftCollectionsLiveData = MutableLiveData<List<CollectionData>>()
    val coinListLiveData = MutableLiveData<List<CoinData>>()

    fun queryCollection(account: ChildAccount) {
        viewModelIOScope(this) {
            queryNft(account)
        }
    }

    fun queryCoinList(account: ChildAccount) {
        viewModelIOScope(this) {
            val tokenList = queryChildAccountTokens(account)
            val coinDataList = mutableListOf<CoinData>()
            val flowCoinList = FlowCoinListManager.coinList().filter { flowCoin ->
                tokenList.firstOrNull {
                    it.id.split("[", "]", ignoreCase = true, limit = 0)[2] == flowCoin.contractName
                } == null
            }
            tokenList.forEach {
                val contractName = it.id.split("[", "]", ignoreCase = true, limit = 0)[2]
                val flowCoin = flowCoinList.firstOrNull { flowCoin ->
                    contractName == flowCoin.contractName
                }
                coinDataList.add(
                    CoinData(
                        flowCoin?.name ?: contractName,
                        flowCoin?.icon.orEmpty().ifBlank {
                            "https://lilico.app/placeholder.png"
                        },
                        it.balance
                    )
                )
            }
            coinListLiveData.postValue(coinDataList)
        }
    }

    private fun queryNft(account: ChildAccount) {
        viewModelIOScope(this) {
            val collections = queryChildAccountNftCollections(account)
            logd("ChildAccountDetailViewModel", collections)
            nftCollectionsLiveData.postValue(collections)
        }
    }

}