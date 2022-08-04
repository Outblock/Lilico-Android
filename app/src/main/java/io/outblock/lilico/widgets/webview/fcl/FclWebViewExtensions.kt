package io.outblock.lilico.widgets.webview.fcl

import android.webkit.WebView
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.lastBlockAccountKeyId
import io.outblock.lilico.manager.flowjvm.transaction.SignPayerResponse
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logv
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.hdWallet
import io.outblock.lilico.wallet.signData
import io.outblock.lilico.widgets.webview.executeJs
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthnResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthzResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclSignMessageResponse

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

fun WebView?.postAuthnViewReadyResponse(fcl: FclAuthnResponse, address: String) {
    ioScope {
        val response = fclAuthnResponse(fcl, address)
        postMessage(response)
    }
}

fun WebView?.postPreAuthzResponse() {
    ioScope {
        val address = walletCache().read()?.primaryWalletAddress() ?: return@ioScope
        postMessage(fclPreAuthzResponse(address))
    }
}

fun WebView?.postAuthzPayloadSignResponse(fcl: FclAuthzResponse) {
    ioScope {
        val address = walletCache().read()?.primaryWalletAddress() ?: return@ioScope
        val signature = hdWallet().signData(fcl.body.message.hexToBytes())
        val keyId = FlowAddress(address).lastBlockAccountKeyId()
        fclAuthzResponse(address, signature, keyId).also { postMessage(it) }
    }
}

fun WebView?.postAuthzEnvelopeSignResponse(sign: SignPayerResponse.EnvelopeSigs) {
    ioScope {
        fclAuthzResponse(sign.address, sign.sig, sign.keyId).also { postMessage(it) }
    }
}

fun WebView?.postSignMessageResponse(fcl: FclSignMessageResponse) {
    ioScope {
        val address = walletCache().read()?.primaryWalletAddress() ?: return@ioScope
        fclSignMessageResponse(fcl, address).also { postMessage(it) }
    }
}