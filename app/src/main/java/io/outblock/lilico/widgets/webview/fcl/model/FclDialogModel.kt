package io.outblock.lilico.widgets.webview.fcl.model

import android.os.Parcelable


@kotlinx.parcelize.Parcelize
data class FclDialogModel(
    val title: String? = null,
    val logo: String? = null,
    val url: String? = null,
    val cadence: String? = null,
    val signMessage: String? = null,
) : Parcelable