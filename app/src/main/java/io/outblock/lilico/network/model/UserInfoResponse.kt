package io.outblock.lilico.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class UserInfoResponse(
    @SerializedName("data")
    val data: UserInfoData,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

@Parcelize
data class UserInfoData(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar")
    val avatar: String,
) : Parcelable