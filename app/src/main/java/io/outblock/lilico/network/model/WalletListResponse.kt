package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

class WalletListResponse(
    @SerializedName("data")
    val data: WalletListData?,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

class WalletListData(
    @SerializedName("id")
    val id: String,
    @SerializedName("primary_wallet")
    val primaryWalletId: Int,
    @SerializedName("user_name")
    val username: String,
    @SerializedName("wallets")
    val wallets: List<WalletData>?
) {
    fun primaryWallet(): WalletData? {
        return wallets?.firstOrNull { it.walletId == primaryWalletId }
    }
}

data class WalletData(
    @SerializedName("blockchain")
    val blockchain: List<BlockchainData>,
    @SerializedName("color")
    val color: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("wallet_id")
    val walletId: Int
)

data class BlockchainData(
    @SerializedName("address")
    val address: String,
    @SerializedName("blockchain_id")
    val blockchainId: Int,
    @SerializedName("chain_id")
    val chainId: String,
    @SerializedName("coins")
    val coins: List<CoinData>,
    @SerializedName("name")
    val name: String
)

data class CoinData(
    @SerializedName("decimal")
    val decimal: Int,
    @SerializedName("is_token")
    val isToken: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("symbol")
    val symbol: String
)