package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("data")
    val data: UserInfoData,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class UserInfoData(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar")
    val avatar: String,
)