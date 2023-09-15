package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.config.NftCollectionConfig
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
            tokenList.forEach {
                val contractName = it.id.split(".", ignoreCase = true, limit = 0)[2]
                val flowCoin = FlowCoinListManager.coinList().firstOrNull { flowCoin ->
                    contractName == flowCoin.contractName
                }
                coinDataList.add(
                    CoinData(
                        flowCoin?.name ?: contractName,
                        flowCoin?.icon.orEmpty().ifBlank {
                            "https://lilico.app/placeholder-2.0.png"
                        },
                        flowCoin?.symbol.orEmpty(),
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
            val collectionList = mutableListOf<CollectionData>()
            collections.forEach{
                val nftCollection = NftCollectionConfig.getByStoragePath(it.path)
                collectionList.add(
                    CollectionData(
                        it.id,
                        nftCollection?.name ?: it.display.name,
                        nftCollection?.logo ?: it.display.squareImage,
                        it.path,
                        it.id.split(".", ignoreCase = true, limit = 0)[2],
                        it.idList
                    )
                )
            }
            nftCollectionsLiveData.postValue(collectionList)
        }
    }

}