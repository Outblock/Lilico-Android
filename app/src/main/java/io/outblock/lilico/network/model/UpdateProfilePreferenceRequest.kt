package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

class UpdateProfilePreferenceRequest(
    @SerializedName("private")
    val isPrivate: Int? = null,
)