package io.outblock.lilico.manager.walletconnect

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.transaction.Signable
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthzDialog
import io.outblock.lilico.widgets.webview.fcl.fclAuthnResponseWithAccountProofSign
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel

private const val TAG = "WalletConnectUtils"

fun Sign.Model.SessionProposal.approveSession() {
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return

    val namespaces = requiredNamespaces.map { item ->
        val caip2Namespace = item.key
        val proposalNamespace = item.value
        val accounts = proposalNamespace.chains.map { "$it:$walletAddress" }
        caip2Namespace to Sign.Model.Namespace.Session(accounts = accounts, proposalNamespace.methods, proposalNamespace.events, extensions = null)
    }.toMap()

    SignClient.approveSession(Sign.Params.Approve(proposerPublicKey, namespaces)) { error -> loge(error.throwable) }
}

fun Sign.Model.SessionProposal.reject() {
    val rejectionReason = "Reject Session"
    val reject = Sign.Params.Reject(
        proposerPublicKey = proposerPublicKey,
        reason = rejectionReason,
        code = 406
    )

    SignClient.rejectSession(reject) { error -> loge(error.throwable) }
}

suspend fun Sign.Model.SessionRequest.dispatch() {
    when (request.method) {
        "flow_authn" -> respondAuthn()
        "flow_authz" -> respondAuthz()
    }
}

private suspend fun Sign.Model.SessionRequest.respondAuthn() {
    val address = walletCache().read()?.primaryWalletAddress() ?: return
    val response = Sign.Params.Response(
        sessionTopic = topic,
        jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(
            request.id,
            fclAuthnResponseWithAccountProofSign(address = address),
        )
    )
    logd(TAG,"respondAuthn: $response")

    SignClient.respond(response) { error -> loge(error.throwable) }
}

private suspend fun Sign.Model.SessionRequest.respondAuthz() {
    val activity = BaseActivity.getCurrentActivity() ?: return
    val json = Gson().fromJson<List<Signable>>(request.params, object : TypeToken<List<Signable>>() {}.type)
    val signable = json.firstOrNull() ?: return
    FclAuthzDialog.show(
        activity.supportFragmentManager,
        FclDialogModel(
            title = peerMetaData?.name,
            logo = peerMetaData?.icons?.firstOrNull(),
            url = peerMetaData?.url,
            cadence = signable.cadence,
        )
    )

    FclAuthzDialog.observe { isApprove->
        if(isApprove){
            val response = Sign.Params.Response(
                sessionTopic = topic,
                jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(
                    request.id,
                    ""
                )
            )

            FclAuthzDialog.dismiss()

            SignClient.respond(response) { error -> loge(error.throwable) }
        }
    }
}
