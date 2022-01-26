package io.outblock.lilico.cache

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.network.model.NFTListData
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.network.model.WalletListData


const val CACHE_NFT_LIST = "nft_list"
const val CACHE_WALLET = "wallet"
const val CACHE_NFT_SELECTION = "nft_selection"

fun nftListCache(address: String?): CacheManager<NFTListData> {
    return CacheManager("${address}_$CACHE_NFT_LIST", NFTListData::class.java)
}

fun walletCache(): CacheManager<WalletListData> {
    return CacheManager(CACHE_WALLET, WalletListData::class.java)
}

fun nftSelectionCache(): CacheManager<NftSelections> {
    return CacheManager(CACHE_NFT_SELECTION, NftSelections::class.java)
}

data class NftSelections(
    @SerializedName("data")
    var data: MutableList<Nft>,
)