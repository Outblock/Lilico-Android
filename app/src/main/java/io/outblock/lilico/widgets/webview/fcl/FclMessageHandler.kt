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
import java.lang.reflect.Type

private val TAG = FclMessageHandler::class.java.simpleName

class FclMessageHandler(
    private val webView: WebView,
) {
    private fun activity() = findActivity(webView) as FragmentActivity

    private fun wallet() = walletCache().read()?.primaryWalletAddress()

    private var message: String = ""

    private var service: String = ""

    fun onHandleMessage(message: String) {
        ioScope { dispatch(message) }
    }

    private suspend fun dispatch(message: String) {
        if (message.isBlank() || message == this.message) {
            return
        }

        if (wallet().isNullOrBlank()) {
            toast(msgRes = R.string.not_logged_in_toast)
            return
        }

        this.message = message
        logd(TAG, message)

        val basicJson = message.fromJson<Map<String, Any>>(object : TypeToken<Map<String, Any>>() {}.type) ?: return

        if (basicJson.isService()) {
            webView.postMessage("{type: '$TYPE_VIEW_READY'}")
            message.fromJson(FclService::class.java)?.let { service = it.service.type }
        } else if (basicJson["type"] as? String == TYPE_VIEW_RESPONSE) {
            dispatchViewReadyResponse(message)
        }
    }

    private suspend fun dispatchViewReadyResponse(message: String) {
        val service = this.service
        val fcl = message.fromJson(FclResponse::class.java) ?: return
        if (service != fcl.serviceType()) {
            return
        }

        when (fcl.service.type) {
            "authn" -> connect(message.fromJson(FclAuthnResponse::class.java)!!)
            "authz" -> cadence(message.fromJson(FclAuthzResponse::class.java)!!)
        }
    }

    private suspend fun cadence(fcl: FclAuthzResponse) {
        val approve = FclAuthzDialog().show(activity().supportFragmentManager, fcl, webView.url, webView.title)
        if (approve) {
            webView.postAuthzPayloadSignResponse(fcl)
        }
    }

    private suspend fun connect(fcl: FclAuthnResponse) {
        val approve = FclAuthnDialog().show(activity().supportFragmentManager, fcl, webView.url, webView.title)
        if (approve) {
            wallet()?.let { webView.postAuthnViewReadyResponse(it) }
        }
    }
}

private fun Map<String, Any>.isService(): Boolean {
    return (this["service"] as? Map<*, *>)?.get("f_type") == "Service"
}

private fun <T> String.fromJson(clz: Class<T>): T? {
    return try {
        Gson().fromJson(this, clz)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

private fun <T> String.fromJson(typeOfT: Type): T? {
    return try {
        Gson().fromJson<T>(this, typeOfT)
    } catch (e: Exception) {
        loge(e)
        null
    }
}