package io.outblock.lilico.manager.drive

import com.google.gson.annotations.SerializedName


class DriveModel(
    val list: List<DriveItem>,
)

class DriveItem(
    @SerializedName("username")
    var username: String,
    @SerializedName("data")
    var data: String,
)

class DriveData(
    @SerializedName("version")
    val version: String,
    @SerializedName("data")
    val data: String,
    @SerializedName("address")
    val address: String,
)