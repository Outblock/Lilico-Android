package io.outblock.lilico.cache

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.app.isMainnet
import io.outblock.lilico.manager.coin.TokenStateCache
import io.outblock.lilico.network.model.*


const val CACHE_NFT_LIST = "nft_list"
const val CACHE_WALLET = "wallet"
const val CACHE_NFT_SELECTION = "nft_selection"
const val USER_INFO = "user_info"
const val ADDRESS_BOOK = "address_book"
const val RECENT_ADDRESS_BOOK = "recent_address_book"
const val TOKEN_STATE = "token_state"

fun nftListCache(address: String?): CacheManager<NFTListData> {
    return CacheManager("${address}_$CACHE_NFT_LIST", NFTListData::class.java)
}

fun walletCache(): CacheManager<WalletListData> {
    return CacheManager("$CACHE_WALLET-${isMainnet()}", WalletListData::class.java)
}

fun nftSelectionCache(): CacheManager<NftSelections> {
    return CacheManager(CACHE_NFT_SELECTION, NftSelections::class.java)
}

fun userInfoCache(): CacheManager<UserInfoData> {
    return CacheManager(USER_INFO, UserInfoData::class.java)
}

fun addressBookCache(): CacheManager<AddressBookContactBookList> {
    return CacheManager(ADDRESS_BOOK, AddressBookContactBookList::class.java)
}

// recent send history
fun recentTransactionCache(): CacheManager<AddressBookContactBookList> {
    return CacheManager(RECENT_ADDRESS_BOOK, AddressBookContactBookList::class.java)
}

fun tokenStateCache(): CacheManager<TokenStateCache> {
    return CacheManager(TOKEN_STATE, TokenStateCache::class.java)
}

data class NftSelections(
    @SerializedName("data")
    var data: MutableList<Nft>,
)