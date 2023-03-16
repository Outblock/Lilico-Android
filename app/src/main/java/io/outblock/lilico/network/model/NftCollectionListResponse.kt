package io.outblock.lilico.network.model


import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.config.NftCollection

data class NftCollectionListResponse(
    @SerializedName("data")
    val data: List<NftCollection>,
    @SerializedName("status")
    val status: Int?
)
