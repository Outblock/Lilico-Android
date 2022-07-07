package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

class FlowScanQueryRequest(
    @SerializedName("query")
    val query: String,
)