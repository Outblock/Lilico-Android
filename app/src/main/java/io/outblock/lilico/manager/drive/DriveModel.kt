package io.outblock.lilico.manager.drive

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


class DriveModel(
    val list: List<DriveItem>,
)

@Parcelize
class DriveItem(
    @SerializedName("username")
    var username: String,
    @SerializedName("data")
    var data: String,
) : Parcelable

class DriveData(
    @SerializedName("version")
    val version: String,
    @SerializedName("data")
    val data: String,
    @SerializedName("address")
    val address: String,
)