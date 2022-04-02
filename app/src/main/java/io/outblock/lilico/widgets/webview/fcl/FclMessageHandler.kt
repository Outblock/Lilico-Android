package io.outblock.lilico.widgets.webview.fcl

import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.safeRun
import io.outblock.lilico.widgets.webview.TYPE_VIEW_READY
import io.outblock.lilico.widgets.webview.TYPE_VIEW_RESPONSE
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthnResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthzResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclService

class FclMessageHandler(
    private val webView: WebView,
) {

    private var serviceType = ""
    private var message = ""

    fun onHandleMessage(data: String) {
        ioScope {
            if (data.isBlank() || message == data) {
                return@ioScope
            }
            logd(TAG, "message:$data")
            message = data

            val json = Gson().fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {}.type) ?: return@ioScope

            if (json.isService()) {
                webView.postMessage("{type: '$TYPE_VIEW_READY'}")
                val service = Gson().fromJson(data, FclService::class.java)
                serviceType = service.service.type
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
            val fcl = Gson().fromJson(data, FclResponse::class.java) ?: return@safeRun
            if (serviceType != fcl.serviceType()) {
                return@safeRun
            }
            when (fcl.service.type) {
                "authn" -> connect(Gson().fromJson(data, FclAuthnResponse::class.java))
                "authz" -> cadence(Gson().fromJson(data, FclAuthzResponse::class.java))
            }
        }
    }

    private fun cadence(fcl: FclAuthzResponse) {
        webView.postAuthzViewReadyResponse(fcl)
    }

    private fun connect(fcl: FclAuthnResponse) {
        // TODO show ui then send response
        walletCache().read()?.primaryWalletAddress()?.let { webView.postAuthnViewReadyResponse(it) }
    }

    private fun Map<String, Any>.isService(): Boolean {
        return (this["service"] as? Map<*, *>)?.get("f_type") == "Service"
    }

    companion object {
        private val TAG = FclMessageHandler::class.java.simpleName
    }
}