package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.config.NftCollection

data class NftCollectionsResponse(
    @SerializedName("data")
    val data: List<NftCollectionWrapper>?,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class NftCollections(
    val collections: List<NftCollectionWrapper> = listOf(),
)

data class NftCollectionWrapper(
    @SerializedName("collection")
    val collection: NftCollection?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("ids")
    val ids: List<Any>?
)