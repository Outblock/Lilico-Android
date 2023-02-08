package io.outblock.lilico.widgets.webview.fcl

import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.R
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.transaction.PayerSignable
import io.outblock.lilico.manager.flowjvm.transaction.SignPayerResponse
import io.outblock.lilico.network.functions.FUNCTION_SIGN_AS_PAYER
import io.outblock.lilico.network.functions.executeHttpFunction
import io.outblock.lilico.utils.*
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthnDialog
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthzDialog
import io.outblock.lilico.widgets.webview.fcl.dialog.FclSignMessageDialog
import io.outblock.lilico.widgets.webview.fcl.model.*
import java.lang.reflect.Type

private val TAG = FclMessageHandler::class.java.simpleName

private var authzTransaction: AuthzTransaction? = null

fun authzTransaction() = authzTransaction

class FclMessageHandler(
    private val webView: WebView,
) {
    private fun activity() = findActivity(webView) as FragmentActivity

    private fun wallet() = walletCache().read()?.walletAddress()

    private var message: String = ""

    private var service: String = ""

    private var fclResponse: FclResponse? = null

    private var readyToSignEnvelope = false

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
        logd(TAG, "message:$message")

        val basicJson = message.fromJson<Map<String, Any>>(object : TypeToken<Map<String, Any>>() {}.type) ?: return

        if (basicJson.isService()) {
            dispatchServiceResponse(message)
        } else if (basicJson["type"] as? String == TYPE_VIEW_RESPONSE) {
            uiScope { dispatchViewReadyResponse(message) }
        }
    }

    private suspend fun dispatchViewReadyResponse(message: String) {
        val service = this.service
        val fcl = message.fromJson(FclSimpleResponse::class.java) ?: return
        if (service != fcl.serviceType()) {
            logd(TAG, "service not same (old:$service, new:${fcl.service})")
            return
        }

        when (fcl.service.type) {
            "authn" -> dispatchAuthn(message.fromJson(FclAuthnResponse::class.java)!!)
            "authz" -> dispatchAuthz(message.fromJson(FclAuthzResponse::class.java)!!)
            "user-signature" -> dispatchSignMessage(message.fromJson(FclSignMessageResponse::class.java)!!)
        }
    }

    private fun dispatchServiceResponse(message: String) {
        message.fromJson(FclService::class.java)?.let {
            service = it.service.type
            fclResponse = null
            if (service == "pre-authz") {
                webView.postPreAuthzResponse()
            } else webView.postMessage("{type: '$TYPE_VIEW_READY'}")
        }
    }

    private suspend fun dispatchAuthn(fcl: FclAuthnResponse) {
        if (fcl.isDispatching()) {
            return
        }
        logd(TAG, "dispatchAuthn")
        fclResponse = fcl
        val approve = FclAuthnDialog().show(
            activity().supportFragmentManager,
            FclDialogModel(title = webView.title, url = webView.url, logo = fcl.config?.app?.icon)
        )
        if (approve) {
            wallet()?.let { webView.postAuthnViewReadyResponse(fcl, it) }
        }
        finishService()
    }

    private suspend fun dispatchAuthz(fcl: FclAuthzResponse) {
        if (fcl.isDispatching()) {
            logd(TAG, "fcl isDispatching:${fcl.uniqueId()}")
            return
        }
        fclResponse = fcl

        if (fcl.body.fType == "Signable") {
            logd(TAG, "roles:${fcl.body.roles}")
        }

        if (fcl.isSignAuthz()) {
            logd(TAG, "signAuthz")
            signAuthz(fcl)
        } else if (fcl.isSignPayload() && !FclAuthzDialog.isShowing()) {
            logd(TAG, "signPayload")
            signPayload(fcl)
        } else if ((readyToSignEnvelope && FclAuthzDialog.isShowing() && fcl.isSignEnvelope())) {
            logd(TAG, "fclSignEnvelope")
            ioScope { signEnvelope(fcl, webView) { FclAuthzDialog.dismiss() } }
        }
    }

    private fun dispatchSignMessage(fcl: FclSignMessageResponse) {
        if (fcl.isDispatching()) {
            logd(TAG, "fcl isDispatching:${fcl.uniqueId()}")
            return
        }
        fclResponse = fcl

        logd(TAG, "dispatchSignMessage:${fcl.uniqueId()}")

        FclSignMessageDialog.show(
            activity().supportFragmentManager,
            FclDialogModel(signMessage = fcl.body?.message, url = webView.url, title = webView.title, logo = fcl.config?.app?.icon)
        )
        FclSignMessageDialog.observe { approve ->
            if (approve) {
                webView.postSignMessageResponse(fcl)
            }
            finishService()
        }
    }

    private fun signAuthz(fcl: FclAuthzResponse) {
        FclAuthzDialog.show(
            activity().supportFragmentManager,
            FclDialogModel(cadence = fcl.body.cadence, url = webView.url, title = webView.title, logo = fcl.config?.app?.icon)
        )
        FclAuthzDialog.observe { approve ->
            if (approve) {
                uiScope { authzTransaction = fcl.toAuthzTransaction(webView) }
                FclAuthzDialog.dismiss()
                webView.postAuthzPayloadSignResponse(fcl)
            }
            finishService()
        }
    }

    private fun signPayload(fcl: FclAuthzResponse) {
        FclAuthzDialog.show(
            activity().supportFragmentManager,
            FclDialogModel(cadence = fcl.body.cadence, url = webView.url, title = webView.title, logo = fcl.config?.app?.icon)
        )
        FclAuthzDialog.observe { approve ->
            readyToSignEnvelope = approve
            if (approve) {
                webView.postAuthzPayloadSignResponse(fcl)
            } else {
                finishService()
            }
        }
    }

    private suspend fun signEnvelope(fcl: FclAuthzResponse, webView: WebView, callback: () -> Unit) {
        val response = executeHttpFunction(
            FUNCTION_SIGN_AS_PAYER, PayerSignable(
                transaction = fcl.body.voucher,
                message = PayerSignable.Message(fcl.body.message)
            )
        )

        safeRun {
            val sign = Gson().fromJson(response, SignPayerResponse::class.java).envelopeSigs

            webView.postAuthzEnvelopeSignResponse(sign)
            uiScope { authzTransaction = fcl.toAuthzTransaction(webView) }

            callback.invoke()
        }
        readyToSignEnvelope = false
        finishService()
    }

    private fun FclResponse.isDispatching(): Boolean = this.uniqueId() == fclResponse?.uniqueId()

    private fun finishService() {
        service = ""
        fclResponse = null
    }
}

private fun Map<String, Any>.isService(): Boolean {
    return this["type"] == null && ((this["service"] as? Map<*, *>)?.get("type") != null || (this["service"] as? Map<*, *>)?.get("f_type") == "Service")
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

private fun FclAuthzResponse.isSignEnvelope(): Boolean {
    return service.type == "authz" && body.fType == "Signable" && body.roles.isSignEnvelope()
}

private fun FclAuthzResponse.isSignAuthz(): Boolean {
    return service.type == "authz" && body.fType == "Signable" && body.roles.isSignAuthz()
}

private fun FclAuthzResponse.isSignPayload(): Boolean {
    return service.type == "authz" && body.fType == "Signable" && body.roles.isSignPayload()
}

private fun FclAuthzResponse.Body.Roles.isSignEnvelope(): Boolean {
    return payer && !authorizer && !proposer
}

private fun FclAuthzResponse.Body.Roles.isSignAuthz(): Boolean {
    return payer && authorizer && proposer
}

private fun FclAuthzResponse.Body.Roles.isSignPayload(): Boolean {
    return !payer && authorizer && proposer
}
