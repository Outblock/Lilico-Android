package io.outblock.lilico.page.window.bubble.model

import io.outblock.lilico.R
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_ADD_TOKEN
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_ENABLE_NFT
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_NFT
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_TRANSFER_COIN
import io.outblock.lilico.manager.transaction.TransactionState.Companion.TYPE_TRANSFER_NFT
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.page.nft.nftlist.cover
import io.outblock.lilico.utils.extensions.res2String

class BubbleItem(
    val data: Any,
)

fun BubbleItem.icon(): Any? {
    return when (data) {
        is BrowserTab -> data.url()?.toFavIcon()
        is TransactionState -> data.icon()
        else -> null
    }
}

fun BubbleItem.title(): String {
    return when (data) {
        is BrowserTab -> data.title().orEmpty()
        is TransactionState -> data.title()
        else -> ""
    }
}

private fun TransactionState.icon(): Any {
    return when (type) {
        TYPE_NFT -> nftData().nft.cover().orEmpty()
        TYPE_TRANSFER_COIN -> FlowCoinListManager.getCoin(coinData().coinSymbol)?.icon.orEmpty()
        TYPE_ADD_TOKEN -> tokenData()?.icon.orEmpty()
        TYPE_ENABLE_NFT -> nftCollectionData()?.logo.orEmpty()
        TYPE_TRANSFER_NFT -> nftSendData().nft.cover().orEmpty()
        else -> ""
    }
}

private fun TransactionState.title(): String {
    return R.string.pending_transaction.res2String()
//    return when (type) {
//        TYPE_NFT -> nftData().nft.cover().orEmpty()
//        TYPE_TRANSFER_COIN -> FlowCoinListManager.getCoin(coinData().coinSymbol)?.icon.orEmpty()
//        TYPE_ADD_TOKEN -> tokenData()?.icon.orEmpty()
//        TYPE_ENABLE_NFT -> nftCollectionData()?.logo.orEmpty()
//        TYPE_TRANSFER_NFT -> nftSendData().nft.cover().orEmpty()
//        else -> ""
//    }
}