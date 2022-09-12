package io.outblock.lilico.manager.walletconnect

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.hexToBytes
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.config.AppConfig
import io.outblock.lilico.manager.flowjvm.lastBlockAccountKeyId
import io.outblock.lilico.manager.flowjvm.transaction.PayerSignable
import io.outblock.lilico.manager.flowjvm.transaction.SignPayerResponse
import io.outblock.lilico.manager.flowjvm.transaction.Signable
import io.outblock.lilico.manager.walletconnect.model.*
import io.outblock.lilico.network.functions.FUNCTION_SIGN_AS_PAYER
import io.outblock.lilico.network.functions.executeHttpFunction
import io.outblock.lilico.utils.*
import io.outblock.lilico.wallet.hdWallet
import io.outblock.lilico.wallet.signData
import io.outblock.lilico.widgets.webview.fcl.dialog.FclAuthzDialog
import io.outblock.lilico.widgets.webview.fcl.dialog.FclSignMessageDialog
import io.outblock.lilico.widgets.webview.fcl.fclAuthzResponse
import io.outblock.lilico.widgets.webview.fcl.fclSignMessageResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel
import java.lang.reflect.Type

private const val TAG = "WalletConnectRequestDispatcher"

suspend fun Sign.Model.SessionRequest.dispatch() {
    when (request.method) {
        WalletConnectMethod.AUTHN.value -> respondAuthn()
        WalletConnectMethod.AUTHZ.value -> respondAuthz()
        WalletConnectMethod.PRE_AUTHZ.value -> respondPreAuthz()
        WalletConnectMethod.USER_SIGNATURE.value -> respondUserSign()
        WalletConnectMethod.SIGN_PAYER.value -> respondSignPayer()
        WalletConnectMethod.SIGN_PROPOSER.value -> respondSignProposer()
    }
}

private fun Sign.Model.SessionRequest.respondAuthn() {
    val address = walletCache().read()?.primaryWalletAddress() ?: return
    val json = gson().fromJson<List<Signable>>(request.params, object : TypeToken<List<Signable>>() {}.type)
    val signable = json.firstOrNull() ?: return
    val services = walletConnectAuthnServiceResponse(address, signable.data?.get("nonce"), signable.data?.get("appIdentifier"))
    val response = Sign.Params.Response(
        sessionTopic = topic,
        jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(request.id, services.responseParse(this))
    )
    logd(TAG, "respondAuthn:\n${services}")

    SignClient.respond(response) { error -> loge(error.throwable) }
    redirectToSourceApp()
}

private fun Sign.Model.SessionRequest.respondAuthz() {
    val activity = BaseActivity.getCurrentActivity() ?: return
    val json = gson().fromJson<List<Signable>>(request.params, object : TypeToken<List<Signable>>() {}.type)
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
    redirectToSourceApp()
}

private fun Sign.Model.SessionRequest.respondPreAuthz() {
    val walletAddress = walletCache().read()?.primaryWalletAddress() ?: return
    val payerAddress = if (AppConfig.isFreeGas()) AppConfig.payer().address else walletAddress
    val response = PollingResponse(
        status = ResponseStatus.APPROVED,
        data = PollingData(
            proposer = Service(
                identity = Identity(address = walletAddress, keyId = 0),
                method = "WC/RPC",
                endpoint = WalletConnectMethod.AUTHZ.value,
            ),
            payer = listOf(
                Service(
                    identity = Identity(address = payerAddress, keyId = 0),
                    method = "WC/RPC",
                    endpoint = WalletConnectMethod.SIGN_PAYER.value,
                )
            ),
            authorization = listOf(
                Service(
                    identity = Identity(address = walletAddress, keyId = 0),
                    method = "WC/RPC",
                    endpoint = WalletConnectMethod.SIGN_PROPOSER.value,
                )
            ),
        )
    )
    approve(gson().toJson(response))
}

private fun Sign.Model.SessionRequest.respondUserSign() {
    val activity = BaseActivity.getCurrentActivity() ?: return
    val address = walletCache().read()?.primaryWalletAddress() ?: return
    val param = gson().fromJson<List<SignableMessage>>(request.params, object : TypeToken<List<SignableMessage>>() {}.type)?.firstOrNull()
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
            redirectToSourceApp()
        }
    }
}

private suspend fun Sign.Model.SessionRequest.respondSignPayer() {
    val json = gson().fromJson<List<Signable>>(request.params, object : TypeToken<List<Signable>>() {}.type)
    val signable = json.firstOrNull() ?: return
    val server = executeHttpFunction(
        FUNCTION_SIGN_AS_PAYER, PayerSignable(
            transaction = signable.voucher!!,
            message = PayerSignable.Message(signable.message!!)
        )
    )

    safeRun {
        val sigs = gson().fromJson(server, SignPayerResponse::class.java).envelopeSigs
        val response = PollingResponse(
            status = ResponseStatus.APPROVED,
            data = PollingData(
                address = sigs.address,
                keyId = sigs.keyId,
                signature = sigs.sig,
            )
        )
        approve(gson().toJson(response))
        FclAuthzDialog.dismiss()
    }
    redirectToSourceApp()
}


private fun Sign.Model.SessionRequest.respondSignProposer() {
    val activity = BaseActivity.getCurrentActivity() ?: return

    logd(TAG, "respondSignProposer param:${request.params}")
    val json = gson().fromJson<List<Signable>>(request.params, object : TypeToken<List<Signable>>() {}.type)
    val signable = json.firstOrNull() ?: return
    val address = walletCache().read()?.primaryWalletAddress() ?: return

    FclAuthzDialog.show(
        activity.supportFragmentManager,
        FclDialogModel(
            title = peerMetaData?.name,
            logo = peerMetaData?.icons?.firstOrNull(),
            url = peerMetaData?.url,
            cadence = signable.voucher?.cadence,
        )
    )
    FclAuthzDialog.observe { approve ->
        if (approve) {
            val response = PollingResponse(
                status = ResponseStatus.APPROVED,
                data = PollingData(
                    address = address,
                    keyId = 0,
                    signature = hdWallet().signData(signable.message!!.hexToBytes())
                )
            )
            approve(gson().toJson(response))
        } else {
            reject()
        }
    }
}

private fun gson() = GsonBuilder().registerTypeAdapter(Signable::class.java, SignableDeserializer()).setLenient().create()

private class SignableDeserializer : JsonDeserializer<Signable> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Signable {
        val obj = json.asJsonObject

        if (obj.has("interaction")) {
            val interaction = obj.get("interaction").asJsonObject
            if (interaction.has("payer")) {
                val payer = obj.get("interaction").asJsonObject.get("payer").asString.trim()
                if (!payer.startsWith("[")) {
                    val ja = JsonArray().apply { add(payer) }
                    interaction.add("payer", ja)
                }
            }
        }
        return Gson().fromJson(obj, Signable::class.java)
    }
}
