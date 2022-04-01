package io.outblock.lilico.widgets.webview

const val ADDRESS_REPLACEMENT = "#address"

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

val FCL_AUTHORIZATION_RESPONSE = """
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