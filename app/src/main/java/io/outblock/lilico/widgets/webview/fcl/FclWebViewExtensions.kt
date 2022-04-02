package io.outblock.lilico.widgets.webview.fcl

import android.webkit.WebView
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logv
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.hdWallet
import io.outblock.lilico.wallet.signData
import io.outblock.lilico.widgets.webview.*
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthzResponse

fun WebView?.postMessage(message: String) {
    uiScope {
        logv("WebView", "postMessage:$message")
        executeJs(
            """
        window && window.postMessage(JSON.parse(JSON.stringify($message || {})), '*')
    """.trimIndent()
        )
    }
}

fun WebView?.postAuthnViewReadyResponse(address: String) {
    uiScope {
        postMessage(FCL_AUTHN_RESPONSE.replace(ADDRESS_REPLACEMENT, address))
    }
}

fun WebView?.postAuthzViewReadyResponse(fcl: FclAuthzResponse) {
    ioScope {
        val address = walletCache().read()?.primaryWalletAddress() ?: return@ioScope
        val signature = hdWallet().signData(fcl.body.message.hexToBytes())
        uiScope {
            postMessage(FCL_AUTHZ_RESPONSE.replace(ADDRESS_REPLACEMENT, address).replace(SIGNATURE_REPLACEMENT, signature))
        }
    }
}