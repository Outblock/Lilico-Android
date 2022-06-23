package io.outblock.lilico.widgets.webview

import com.nftco.flow.sdk.FlowAddress
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.config.isGasFree
import io.outblock.lilico.manager.flowjvm.lastBlockAccountKeyId
import io.outblock.lilico.wallet.toAddress

const val PRE_AUTHZ_REPLACEMENT = "#pre-authz"
const val ADDRESS_REPLACEMENT = "#address"
const val PAYER_ADDRESS_REPLACEMENT = "#payer-address"
const val SIGNATURE_REPLACEMENT = "#signature"

private const val SERVICE = """
    {
      f_type: 'Service',
      f_vsn: '1.0.0',
      type: 'authn',
      uid: 'Lilico',
      endpoint: 'ext:0x000',
      method: 'EXT/RPC',
      id: '64554be',
      identity: {
        address: '0x64554be',
      },
      provider: {
        address: '0x64554be',
        name: 'Lilico',
        icon: 'https://raw.githubusercontent.com/Outblock/Lilico-Web/main/asset/logo-dis.png',
        description: 'Lilico extension wallet',
      },
    }
"""

val FCL_AUTHN_RESPONSE = """
    {
      "f_type": "PollingResponse",
      "f_vsn": "1.0.0",
      "status": "APPROVED",
      "reason": null,
      "data": {
        "f_type": "AuthnResponse",
        "f_vsn": "1.0.0",
        "addr": "$ADDRESS_REPLACEMENT",
        "services": [
          {
            "f_type": "Service",
            "f_vsn": "1.0.0",
            "type": "authn",
            "uid": "lilicoWallet#authn",
            "endpoint": "ext:0x000",
            "id": "$ADDRESS_REPLACEMENT",
            "identity": {
              "address": "$ADDRESS_REPLACEMENT"
            },
            "provider": {
              "f_type": "ServiceProvider",
              "f_vsn": "1.0.0",
              "address": "$ADDRESS_REPLACEMENT",
              "name": "Lilico Wallet"
            }
          },
          $PRE_AUTHZ_REPLACEMENT
          {
            "f_type": "Service",
            "f_vsn": "1.0.0",
            "type": "authz",
            "uid": "lilicoWallet#authz",
            "endpoint": "ext:0x000",
            "method": "EXT/RPC",
            "identity": {
              "address": "$ADDRESS_REPLACEMENT",
              "keyId": 0
            }
          }
        ],
        "paddr": null
      },
      "type": "FCL:VIEW:RESPONSE"
    }
""".trimIndent()

val FCL_AUTHZ_RESPONSE = """
    {
      "f_type": "PollingResponse",
      "f_vsn": "1.0.0",
      "status": "APPROVED",
      "reason": null,
      "data": {
        "f_type": "CompositeSignature",
        "f_vsn": "1.0.0",
        "addr": "$ADDRESS_REPLACEMENT",
        "keyId": 0,
        "signature": "$SIGNATURE_REPLACEMENT"
      },
      "type": "FCL:VIEW:RESPONSE"
    }
""".trimIndent()

val FCL_PRE_AUTHZ_RESPONSE = """
    {
        "status": "APPROVED",
        "data": {
            "f_type": "PreAuthzResponse",
            "f_vsn": "1.0.0",
            "proposer": {
                "f_type": "Service",
                "f_vsn": "1.0.0",
                "type": "authz",
                "uid": "lilico#authz",
                "endpoint": "chrome-extension://hpclkefagolihohboafpheddmmgdffjm/popup.html",
                "method": "EXT/RPC",
                "identity": {
                    "address": "$ADDRESS_REPLACEMENT",
                    "keyId": 0
                }
            },
            "payer": [
                {
                    "f_type": "Service",
                    "f_vsn": "1.0.0",
                    "type": "authz",
                    "uid": "lilico#authz",
                    "endpoint": "chrome-extension://hpclkefagolihohboafpheddmmgdffjm/popup.html",
                    "method": "EXT/RPC",
                    "identity": {
                        "address": "$PAYER_ADDRESS_REPLACEMENT",
                        "keyId": 0
                    }
                }
            ],
            "authorization": [
                {
                    "f_type": "Service",
                    "f_vsn": "1.0.0",
                    "type": "authz",
                    "uid": "lilico#authz",
                    "endpoint": "chrome-extension://hpclkefagolihohboafpheddmmgdffjm/popup.html",
                    "method": "EXT/RPC",
                    "identity": {
                        "address": "$ADDRESS_REPLACEMENT",
                        "keyId": 0
                    }
                }
            ]
        },
        "type": "FCL:VIEW:RESPONSE"
    }
""".trimIndent()

// inject lilico auth login
val JS_FCL_EXTENSIONS = """
    if (!Array.isArray(window.fcl_extensions)) {
      window.fcl_extensions = []
    }
    window.fcl_extensions.push($SERVICE)
""".trimIndent()

val JS_LISTEN_WINDOW_FCL_MESSAGE = """
    window.addEventListener('message', function (event) {
      window.android.message(JSON.stringify(event.data))
    })
""".trimIndent()

val JS_LISTEN_FLOW_TRANSACTION_MESSAGE = """
    window.addEventListener('FLOW::TX', function (event) {
      window.android.message(JSON.stringify({type: 'FLOW::TX', ...event.detail}))
    })
""".trimIndent()

val JS_LISTEN_FLOW_WALLET_TRANSACTION = """
    window.addEventListener('FLOW::TX', function (event) {
        walletController.listenTransaction(msg.txId, false);
    })
""".trimIndent()

val JS_QUERY_WINDOW_COLOR = """
    var color = window.getComputedStyle( document.body ,null).getPropertyValue('background-color');
    rgb2Hex = s => s.match(/[0-9]+/g).reduce((a, b) => a+(b|256).toString(16).slice(1), '#');
    window.android.windowColor(rgb2Hex(color));
""".trimIndent()

suspend fun generateAuthnPreAuthz(): String {
    return if (isGasFree()) {
        """
            {
                "f_type": "Service",
                "f_vsn": "1.0.0",
                "type": "pre-authz",
                "uid": "lilico#pre-authz",
                "endpoint": "android://pre-authz.lilico.app",
                "method": "EXT/RPC",
                "data": {
                    "address": "${GasConfig.payer().address.toAddress()}",
                    "keyId": ${FlowAddress(GasConfig.payer().address.toAddress()).lastBlockAccountKeyId()}
                }
            },
        """.trimIndent()
    } else ""
}