package io.outblock.lilico.widgets.webview

import android.webkit.WebView
import com.just.agentweb.AgentWeb
import io.outblock.lilico.utils.logv
import io.outblock.lilico.utils.uiScope


fun WebView?.executeJs(script: String) {
    this?.loadUrl("javascript:$script")
}

fun AgentWeb?.callJs(functionName: String, params: List<String>? = null) {
    if (params.isNullOrEmpty()) {
        this?.jsAccessEntrace?.quickCallJs(functionName)
    } else {
        this?.jsAccessEntrace?.quickCallJs(functionName, *params.toTypedArray())
    }
}

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
        postMessage(FCL_AUTHORIZATION_RESPONSE.replace(ADDRESS_REPLACEMENT, address))
    }
}