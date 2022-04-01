package io.outblock.lilico.widgets.webview

import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.safeRun
import io.outblock.lilico.widgets.webview.model.FclViewReadyResponse

class FclMessageHandler(
    private val webView: WebView,
) {

    fun onHandleMessage(data: String) {
        ioScope {
            val json = Gson().fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {}.type) ?: return@ioScope

            if (json.isService()) {
                webView.postMessage("{type: '$TYPE_VIEW_READY'}")
                return@ioScope
            }

            when (json["type"] as String) {
                // TODO show ui then send response
                TYPE_VIEW_RESPONSE -> dispatchViewReadyResponse(data)
            }

        }
    }

    private fun dispatchViewReadyResponse(data: String) {
        safeRun {
            val fcl = Gson().fromJson(data, FclViewReadyResponse::class.java) ?: return@safeRun
            when (fcl.service.type) {
                "authn" -> connect(fcl)
                "authz" -> transaction(fcl)
            }
        }
    }

    private fun transaction(fcl: FclViewReadyResponse) {
    }

    private fun connect(fcl: FclViewReadyResponse) {
        // TODO show ui then send response
        walletCache().read()?.primaryWalletAddress()?.let { webView.postAuthnViewReadyResponse(it) }
    }

    private fun Map<String, Any>.isService(): Boolean {
        return (this["service"] as? Map<*, *>)?.get("f_type") == "Service"
    }
}