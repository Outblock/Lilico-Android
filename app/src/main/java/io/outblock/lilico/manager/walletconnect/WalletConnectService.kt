package io.outblock.lilico.manager.walletconnect

import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.FlowAddress
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.flowjvm.lastBlockAccountKeyId
import io.outblock.lilico.wallet.removeAddressPrefix
import io.outblock.lilico.wallet.toAddress

@WorkerThread
fun walletConnectAuthnServiceResponse(
    address: String,
): String {
    return """
{
  "f_type": "PollingResponse",
  "status": "APPROVED",
  "f_vsn": "1.0.0",
  "data": {
    "fVsn": "1.0.0",
    "paddr": null,
    "services": [
      ${authn(address.removeAddressPrefix())},
      ${authz(address.removeAddressPrefix())},
      ${userSign(address.removeAddressPrefix())}
    ],
    "addr": "${address.toAddress()}",
    "fType": "AuthnResponse"
  }
}
    """.trimIndent()
}

private fun authn(address: String): String {
    return """
{
    "f_type": "Service",
    "uid": "flow-wallet#authn",
    "provider": {
        "f_type": "ServiceProvider",
        "f_vsn": "1.0.0",
        "name": "Flow Wallet",
        "address": "$address"
    },
    "id": "$address",
    "f_vsn": "1.0.0",
    "endpoint": "flow_authn",
    "type": "authn",
    "identity": { "address": "$address", "keyId": 0 }
}
    """.trimIndent()
}

private fun authz(address: String): String {
    return """
{
    "f_type": "Service",
    "method": "WC/RPC",
    "uid": "flow-wallet#authz",
    "f_vsn": "1.0.0",
    "endpoint": "flow_authz",
    "type": "authz",
    "identity": { "address": "$address", "keyId": 0 }
}
    """.trimIndent()
}

private fun userSign(address: String): String {
    return """
{
    "f_type": "Service",
    "method": "WC/RPC",
    "uid": "flow-wallet#user-signature",
    "f_vsn": "1.0.0",
    "endpoint": "flow_user_sign",
    "type": "user-signature",
    "identity": { "address": "$address", "keyId": 0 }
}
    """.trimIndent()
}

private fun preAuthz(): String {
    return """
{
    "f_type": "Service",
    "f_vsn": "1.0.0",
    "type": "pre-authz",
    "uid": "lilico#pre-authz",
    "endpoint": "flow_pre_authz",
    "method": "WC/RPC",
    "data": {
      "address": "${GasConfig.payer().address.toAddress()}",
      "keyId": ${FlowAddress(GasConfig.payer().address.toAddress()).lastBlockAccountKeyId()}
    }
}
    """.trimIndent()
}
