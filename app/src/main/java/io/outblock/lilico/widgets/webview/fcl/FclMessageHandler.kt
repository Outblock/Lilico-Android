package io.outblock.lilico.widgets.webview.fcl

import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.R
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.utils.*
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthnDialog
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthzDialog
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthnResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthzResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclService

class FclMessageHandler(
    private val webView: WebView,
) {

    private val activity by lazy { findActivity(webView) as FragmentActivity }

    private val walletAddress by lazy { walletCache().read()?.primaryWalletAddress() }

    private var serviceType = ""
    private var message = ""

    fun onHandleMessage(data: String) {
        ioScope {
            if (data.isBlank() || message == data) {
                return@ioScope
            }
            logd(TAG, "message:$data")
            message = data

            runCatching {
                val json = Gson().fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {}.type) ?: return@ioScope

                if (json.isService()) {
                    webView.postMessage("{type: '$TYPE_VIEW_READY'}")
                    val service = Gson().fromJson(data, FclService::class.java)
                    serviceType = service.service.type
                    return@ioScope
                }

                if (walletAddress.isNullOrBlank()) {
                    toast(msgRes = R.string.not_logged_in_toast)
                    return@ioScope
                }

                when (json["type"] as String) {
                    TYPE_VIEW_RESPONSE -> dispatchViewReadyResponse(data)
                }
            }
        }
    }

    private fun dispatchViewReadyResponse(data: String) {
        safeRun {
            val fcl = Gson().fromJson(data, FclResponse::class.java) ?: return@safeRun
            uiScope {
                if (serviceType != fcl.serviceType()) {
                    return@uiScope
                }
                serviceType = ""
                when (fcl.service.type) {
                    "authn" -> connect(Gson().fromJson(data, FclAuthnResponse::class.java))
                    "authz" -> cadence(Gson().fromJson(data, FclAuthzResponse::class.java))
                }
            }
        }
    }

    private suspend fun cadence(fcl: FclAuthzResponse) {
        val approve = FclAuthzDialog().show(activity.supportFragmentManager, fcl, webView.url, webView.title)
        if (approve) {
            webView.postAuthzViewReadyResponse(fcl)
        }
    }

    private suspend fun connect(fcl: FclAuthnResponse) {
        val approve = FclAuthnDialog().show(activity.supportFragmentManager, fcl, webView.url, webView.title)
        if (approve) {
            walletAddress?.let { webView.postAuthnViewReadyResponse(it) }
        }
    }

    private fun Map<String, Any>.isService(): Boolean {
        return (this["service"] as? Map<*, *>)?.get("f_type") == "Service"
    }

    companion object {
        private val TAG = FclMessageHandler::class.java.simpleName
    }
}