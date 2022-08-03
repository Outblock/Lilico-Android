package io.outblock.lilico.page.inbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nftco.flow.sdk.FlowTransactionStatus
import io.outblock.lilico.R
import io.outblock.lilico.cache.inboxCache
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.OnCoinRateUpdate
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.flowjvm.cadenceClaimInboxNft
import io.outblock.lilico.manager.flowjvm.cadenceClaimInboxToken
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.OtherHostService
import io.outblock.lilico.network.model.InboxNft
import io.outblock.lilico.network.model.InboxToken
import io.outblock.lilico.network.retrofitWithHost
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.toSafeInt

class InboxViewModel : ViewModel(), OnCoinRateUpdate, OnTransactionStateChange {

    val tokenListLiveData = MutableLiveData<List<InboxToken>>()
    val nftListLiveData = MutableLiveData<List<InboxNft>>()

    val claimExecutingLiveData = MutableLiveData<Boolean>()

    init {
        CoinRateManager.addListener(this)
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    override fun onTransactionStateChange() {
        query()
    }

    fun query() {
        viewModelIOScope(this) {
            queryCache()
            queryServer()
        }
    }

    override fun onCoinRateUpdate(coin: FlowCoin, price: Float) {
        val tokens = tokenListLiveData.value.orEmpty().toMutableList()
        tokens.toList().forEachIndexed { index, token ->
            if (token.coinAddress == coin.address()) {
                tokens[index] = token.copy(marketValue = token.amount * price)
            }
        }
        tokenListLiveData.postValue(tokens)
    }

    fun claimToken(token: InboxToken) {
        claimExecutingLiveData.postValue(true)
        viewModelIOScope(this) {
            try {
                val coin = FlowCoinListManager.coinList().firstOrNull { it.address() == token.coinAddress }!!
                val txid = cadenceClaimInboxToken(meowDomainHost()!!, token.key, coin, token.amount)!!
                val transactionState = TransactionState(
                    transactionId = txid,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_TRANSACTION_DEFAULT,
                    data = Gson().toJson(token)
                )
                TransactionStateManager.newTransaction(transactionState)
                pushBubbleStack(transactionState)
            } catch (e: Exception) {
                loge(e)
                toast(msgRes = R.string.claim_failed)
            }
            claimExecutingLiveData.postValue(false)
        }
    }

    fun claimNft(nft: InboxNft) {
        claimExecutingLiveData.postValue(true)
        viewModelIOScope(this) {
            try {
                val collection = NftCollectionConfig.get(nft.collectionAddress)!!
                val txid = cadenceClaimInboxNft(meowDomainHost()!!, nft.key, collection, nft.tokenId.toSafeInt())!!
                val transactionState = TransactionState(
                    transactionId = txid,
                    time = System.currentTimeMillis(),
                    state = FlowTransactionStatus.PENDING.num,
                    type = TransactionState.TYPE_TRANSACTION_DEFAULT,
                    data = Gson().toJson(nft)
                )
                TransactionStateManager.newTransaction(transactionState)
                pushBubbleStack(transactionState)
            } catch (e: Exception) {
                loge(e)
                toast(msgRes = R.string.claim_failed)
            }
            claimExecutingLiveData.postValue(false)
        }
    }

    private suspend fun queryServer() {
        val domain = meowDomain() ?: return
        val service = retrofitWithHost(if (isTestnet()) "https://testnet.flowns.io/" else "https://flowns.io").create(OtherHostService::class.java)
        val response = service.queryInbox(domain)
        tokenListLiveData.postValue(response.tokenList())
        nftListLiveData.postValue(response.nftList())
        updateInboxReadList(response)
        response.tokenList().mapNotNull { token -> FlowCoinListManager.coinList().firstOrNull { it.address() == token.coinAddress } }
            .forEach { CoinRateManager.fetchCoinRate(it) }
        inboxCache().cache(response)
    }

    private fun queryCache() {
        val response = inboxCache().read() ?: return
        tokenListLiveData.postValue(response.tokenList())
        nftListLiveData.postValue(response.nftList())
        response.tokenList().mapNotNull { token -> FlowCoinListManager.coinList().firstOrNull { it.address() == token.coinAddress } }
            .forEach { CoinRateManager.fetchCoinRate(it) }
    }
}