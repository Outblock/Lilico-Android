package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("data")
    val data: SearchUsers,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class SearchUsers(
    val users: List<UserInfoData>? = null,
)
