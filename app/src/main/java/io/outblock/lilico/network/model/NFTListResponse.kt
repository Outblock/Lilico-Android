package io.outblock.lilico.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class NFTListResponse(
    @SerializedName("data")
    val data: NFTListData,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class NFTListData(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("network")
    val network: String,
    @SerializedName("nftCount")
    val nftCount: Int,
    @SerializedName("nfts")
    val nfts: List<Nft>,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("ownerAddress")
    val ownerAddress: String
)

@Parcelize
data class Nft(
    @SerializedName("contract")
    val contract: NFTContract,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: NFTId,
    @SerializedName("media")
    val media: NFTMedia?,
    @SerializedName("metadata")
    val metadata: NFTMetadata,
    @SerializedName("title")
    val title: String?,
) : Parcelable {
    fun uniqueId() = "${contract.address}-${id.tokenId}"
}

@Parcelize
data class NFTContract(
    @SerializedName("address")
    val address: String,
    @SerializedName("contractMetadata")
    val contractMetadata: NFTContractMetadata,
    @SerializedName("externalDomain")
    val externalDomain: String,
    @SerializedName("name")
    val name: String?,
) : Parcelable

@Parcelize
data class NFTContractMetadata(
    @SerializedName("publicCollectionName")
    val publicCollectionName: String,
    @SerializedName("publicPath")
    val publicPath: String,
    @SerializedName("storagePath")
    val storagePath: String
) : Parcelable

@Parcelize
data class NFTId(
    @SerializedName("tokenId")
    val tokenId: String,
    @SerializedName("tokenMetadata")
    val tokenMetadata: NFTTokenMetadata
) : Parcelable

@Parcelize
data class NFTMetadata(
    @SerializedName("metadata")
    val metadata: List<NFTMetadataX>
) : Parcelable

@Parcelize
data class NFTMetadataX(
    @SerializedName("name")
    val name: String,
    @SerializedName("value")
    val value: String
) : Parcelable

@Parcelize
data class NFTMedia(
    @SerializedName("mimeType")
    val mimeType: String,
    @SerializedName("uri")
    val uri: String
) : Parcelable

@Parcelize
data class NFTTokenMetadata(
    @SerializedName("uuid")
    val uuid: String
) : Parcelable