package io.outblock.lilico.page.addtoken

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.page.addtoken.model.TokenItem
import io.outblock.lilico.utils.viewModelIOScope

class AddTokenViewModel : ViewModel() {

    val tokenListLiveData = MutableLiveData<List<TokenItem>>()

    private val coinList = mutableListOf<TokenItem>()

    private var keyword: String = ""

    fun load() {
        viewModelIOScope(this) {
            coinList.clear()
            coinList.addAll(
                FlowCoinListManager.coinList().map { TokenItem(coin = it, isAdded = TokenStateManager.isTokenAdded(it.address()), isAdding = false) })
            tokenListLiveData.postValue(coinList.toList())
        }
    }

    fun search(keyword: String) {
        tokenListLiveData.postValue(coinList.filter {
            it.coin.name.lowercase().contains(keyword.lowercase()) || it.coin.symbol.lowercase().contains(keyword.lowercase())
        })
    }

    fun clearSearch() {
        tokenListLiveData.postValue(coinList.toList())
    }

    fun addToken(coin: FlowCoin) {

    }
}