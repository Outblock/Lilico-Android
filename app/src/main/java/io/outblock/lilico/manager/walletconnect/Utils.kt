package io.outblock.lilico.manager.walletconnect

import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.bytesToHex
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.manager.walletconnect.model.WCRequest
import io.outblock.lilico.utils.Env
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge

private const val TAG = "WalletConnectUtils"

fun Sign.Model.SessionProposal.approveSession() {
    val walletAddress = WalletManager.wallet()?.walletAddress() ?: return

    val namespaces = requiredNamespaces.map { item ->
        val caip2Namespace = item.key
        val proposalNamespace = item.value
        val accounts = proposalNamespace.chains?.map { "$it:$walletAddress" }.orEmpty()
//        val methods = proposalNamespace.methods.toMutableSet().apply { add("flow_pre_authz") }
        caip2Namespace to Sign.Model.Namespace.Session(
            chains = proposalNamespace.chains,
            accounts = accounts,
            methods = proposalNamespace.methods,
            events = proposalNamespace.events
        )
    }.toMap()
    logd(TAG, "approveSession: $namespaces")
    SignClient.approveSession(Sign.Params.Approve(proposerPublicKey, namespaces)) { error -> loge(error.throwable) }
}

fun Sign.Model.SessionProposal.reject() {
    val rejectionReason = "Reject Session"
    val reject = Sign.Params.Reject(
        proposerPublicKey = proposerPublicKey,
        reason = rejectionReason,
    )

    SignClient.rejectSession(reject) { error -> loge(error.throwable) }
}

internal fun WCRequest.approve(result: String) {
    logd(TAG, "SessionRequest.approve:$result")
    val response = Sign.Params.Response(
        sessionTopic = topic,
        jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(requestId, result.responseParse(this))
    )
    SignClient.respond(response) { error -> loge(error.throwable) }
}

internal fun WCRequest.reject() {
    SignClient.respond(
        Sign.Params.Response(
            sessionTopic = topic,
            jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcError(requestId, 0, "User rejected")
        )
    ) { error -> loge(error.throwable) }
}

internal fun String.responseParse(model: WCRequest): String {
    if (model.isFromFclSdk()) {
        return this.toByteArray().bytesToHex()
    }
    return this
}

internal fun WCRequest.isFromFclSdk(): Boolean {
    return metaData?.redirect?.contains("\$fromSdk") == true
}

internal fun WCRequest.redirectToSourceApp() {
    if (!isFromFclSdk()) {
        return
    }
    val context = BaseActivity.getCurrentActivity() ?: Env.getApp()
    val intent = context.packageManager.getLaunchIntentForPackage(metaData?.redirect!!.split("\$").first())
    context.startActivity(intent)
}

internal class SignableMessage(
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("message")
    val message: String?,
)
