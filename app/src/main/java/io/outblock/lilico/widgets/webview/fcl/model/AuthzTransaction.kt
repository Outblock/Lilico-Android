package io.outblock.lilico.widgets.webview.fcl.model

import android.webkit.WebView
import io.outblock.lilico.manager.flowjvm.transaction.Voucher

data class AuthzTransaction(
    val url: String?,
    val title: String?,
    val voucher: Voucher,
)

fun FclAuthzResponse.toAuthzTransaction(
    webView: WebView,
): AuthzTransaction {
    return AuthzTransaction(
        url = webView.url,
        title = webView.title,
        voucher = body.voucher,
    )
}