package io.outblock.lilico.cache

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.app.isMainnet
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.manager.coin.TokenStateCache
import io.outblock.lilico.manager.nft.NftCollectionStateCache
import io.outblock.lilico.network.model.*
import io.outblock.lilico.page.transaction.record.model.TransactionRecordList


const val CACHE_NFT_LIST = "nft_list"
const val CACHE_WALLET = "wallet"
const val USER_INFO = "user_info"
const val ADDRESS_BOOK = "address_book"
const val RECENT_ADDRESS_BOOK = "recent_address_book"
const val TOKEN_STATE = "token_state"
const val NFT_COLLECTION_STATE = "nft_collection_state"

fun nftListCache(address: String?): CacheManager<NFTListData> {
    return CacheManager("${address}_$CACHE_NFT_LIST".cacheFile(), NFTListData::class.java)
}

fun walletCache(): CacheManager<WalletListData> {
    return CacheManager("$CACHE_WALLET-${isMainnet()}", WalletListData::class.java)
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

fun nftCollectionStateCache(): CacheManager<NftCollectionStateCache> {
    return CacheManager(NFT_COLLECTION_STATE, NftCollectionStateCache::class.java)
}

fun inboxCache(): CacheManager<InboxResponse> {
    return CacheManager("inbox_response".cacheFile(), InboxResponse::class.java)
}

fun transactionRecordCache(): CacheManager<TransactionRecordList> {
    return CacheManager("transaction_record".cacheFile(), TransactionRecordList::class.java)
}


fun String.cacheFile() = "${this.hashCode()}.${if (isTestnet()) "t" else "m"}"

data class NftSelections(
    @SerializedName("data")
    var data: MutableList<Nft>,
)