package io.outblock.lilico.manager.walletconnect

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.hexToBytes
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.lastBlockAccountKeyId
import io.outblock.lilico.manager.flowjvm.transaction.Signable
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.wallet.hdWallet
import io.outblock.lilico.wallet.signData
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthzDialog
import io.outblock.lilico.widgets.webview.fcl.dialog.FclSignMessageDialog
import io.outblock.lilico.widgets.webview.fcl.fclAuthzResponse
import io.outblock.lilico.widgets.webview.fcl.fclSignMessageResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel

private const val TAG = "WalletConnectUtils"

fun Sign.Model.SessionProposal.approveSession() {
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return

    val namespaces = requiredNamespaces.map { item ->
        val caip2Namespace = item.key
        val proposalNamespace = item.value
        val accounts = proposalNamespace.chains.map { "$it:$walletAddress" }
//        val methods = proposalNamespace.methods.toMutableSet().apply { add("flow_pre_authz") }
        caip2Namespace to Sign.Model.Namespace.Session(accounts = accounts, proposalNamespace.methods, proposalNamespace.events, extensions = null)
    }.toMap()
    logd(TAG, "approveSession: $namespaces")
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
        "flow_user_sign" -> respondUserSign()
    }
}

private fun Sign.Model.SessionRequest.respondAuthn() {
    val address = walletCache().read()?.primaryWalletAddress() ?: return
    val services = walletConnectAuthnServiceResponse(address)
    val response = Sign.Params.Response(
        sessionTopic = topic,
        jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(request.id, services)
    )
    logd(TAG, "respondAuthn:\n${services}")

    SignClient.respond(response) { error -> loge(error.throwable) }
}

private suspend fun Sign.Model.SessionRequest.respondAuthz() {
    val activity = BaseActivity.getCurrentActivity() ?: return
    logd("xxx", request.params.firstOrNull())
    val json = Gson().fromJson<List<Signable>>(request.params, object : TypeToken<List<Signable>>() {}.type)
    val signable = json.firstOrNull() ?: return
    val message = signable.message ?: return
    uiScope {
        FclAuthzDialog.show(
            activity.supportFragmentManager,
            FclDialogModel(
                title = peerMetaData?.name,
                logo = peerMetaData?.icons?.firstOrNull(),
                url = peerMetaData?.url,
                cadence = signable.cadence,
            )
        )

        FclAuthzDialog.observe { isApprove ->
            ioScope {
                val address = walletCache().read()?.primaryWalletAddress() ?: return@ioScope
                val signature = hdWallet().signData(message.hexToBytes())
                val keyId = FlowAddress(address).lastBlockAccountKeyId()

                if (isApprove) approve(fclAuthzResponse(address, signature, keyId)) else reject()
                uiScope { FclAuthzDialog.dismiss() }
            }
        }
    }
}

private fun Sign.Model.SessionRequest.respondUserSign() {
    val activity = BaseActivity.getCurrentActivity() ?: return
    val address = walletCache().read()?.primaryWalletAddress() ?: return
    val param = Gson().fromJson<List<SignableMessage>>(request.params, object : TypeToken<List<SignableMessage>>() {}.type)?.firstOrNull()
    val message = param?.message ?: return
    uiScope {
        FclSignMessageDialog.show(
            activity.supportFragmentManager,
            FclDialogModel(
                title = peerMetaData?.name,
                logo = peerMetaData?.icons?.firstOrNull(),
                url = peerMetaData?.url,
                signMessage = message,
            )
        )

        FclSignMessageDialog.observe { isApprove ->
            if (isApprove) approve(fclSignMessageResponse(message, address)) else reject()
            FclAuthzDialog.dismiss()
        }
    }
}

private fun Sign.Model.SessionRequest.approve(result: String) {
    val response = Sign.Params.Response(
        sessionTopic = topic,
        jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(request.id, result)
    )
    SignClient.respond(response) { error -> loge(error.throwable) }
}

private fun Sign.Model.SessionRequest.reject() {
    SignClient.respond(
        Sign.Params.Response(
            sessionTopic = topic,
            jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcError(request.id, 0, "User rejected")
        )
    ) { error -> loge(error.throwable) }
}

private class SignableMessage(
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("message")
    val message: String?,
)
