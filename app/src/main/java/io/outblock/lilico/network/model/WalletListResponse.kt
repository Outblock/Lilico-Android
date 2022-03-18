package io.outblock.lilico.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class WalletListResponse(
    @SerializedName("data")
    val data: WalletListData?,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class WalletListData(
    @SerializedName("id")
    val id: String,
    @SerializedName("primary_wallet")
    val primaryWalletId: Int,
    @SerializedName("username")
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
    @SerializedName("id")
    val walletId: Int
)

@Parcelize
data class BlockchainData(
    @SerializedName("address")
    val address: String,
    @SerializedName("blockchain_id")
    val blockchainId: Int,
    @SerializedName("chain_id")
    val chainId: String,
    @SerializedName("coins")
    val coins: List<String>,
    @SerializedName("name")
    val name: String
) : Parcelable

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