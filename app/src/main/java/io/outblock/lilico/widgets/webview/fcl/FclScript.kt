package io.outblock.lilico.widgets.webview.fcl

import com.nftco.flow.sdk.DomainTag
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.config.isGasFree
import io.outblock.lilico.manager.flowjvm.lastBlockAccountKeyId
import io.outblock.lilico.wallet.hdWallet
import io.outblock.lilico.wallet.signData
import io.outblock.lilico.wallet.toAddress
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthnResponse
import io.outblock.lilico.widgets.webview.fcl.model.FclSignMessageResponse

private const val PRE_AUTHZ_REPLACEMENT = "#pre-authz"
private const val ADDRESS_REPLACEMENT = "#address"
private const val KEY_ID_REPLACEMENT = "#key-id"
private const val PAYER_ADDRESS_REPLACEMENT = "#payer-address"
private const val SIGNATURE_REPLACEMENT = "#signature"
private const val USER_SIGNATURE_REPLACEMENT = "#user-signature"
private const val ACCOUNT_PROOF_REPLACEMENT = "#account-proof"
private const val NONCE_REPLACEMENT = "#nonce"


const val FCL_AUTHN_SERVICE = """
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

private val FCL_AUTHN_RESPONSE = """
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
          $USER_SIGNATURE_REPLACEMENT
          $ACCOUNT_PROOF_REPLACEMENT
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

private val FCL_AUTHN_RESPONSE_USER_SIGNATURE = """
    {
        "f_type": "Service",
        "f_vsn": "1.0.0",
        "type": "user-signature",
        "uid": "lilico#user-signature",
        "endpoint": "chrome-extension://hpclkefagolihohboafpheddmmgdffjm/popup.html",
        "method": "EXT/RPC"
    },
""".trimIndent()

private val FCL_AUTHN_RESPONSE_ACCOUNT_PROOF = """
    {
        "f_type": "Service",
        "f_vsn": "1.0.0",
        "type": "account-proof",
        "uid": "lilico#account-proof",
        "endpoint": "chrome-extension://hpclkefagolihohboafpheddmmgdffjm/popup.html",
        "method": "EXT/RPC",
        "data": {
          "f_type": "account-proof",
          "f_vsn": "2.0.0",
          "address": "$ADDRESS_REPLACEMENT",
          "nonce": "$NONCE_REPLACEMENT",
          "signatures": [
            {
              "f_type": "CompositeSignature",
              "f_vsn": "1.0.0",
              "addr": "$ADDRESS_REPLACEMENT",
              "keyId": 0,
              "signature": "$SIGNATURE_REPLACEMENT"
            }
          ]
        }
    },
""".trimIndent()

private val FCL_AUTHZ_RESPONSE = """
    {
      "f_type": "PollingResponse",
      "f_vsn": "1.0.0",
      "status": "APPROVED",
      "reason": null,
      "data": {
        "f_type": "CompositeSignature",
        "f_vsn": "1.0.0",
        "addr": "$ADDRESS_REPLACEMENT",
        "keyId": $KEY_ID_REPLACEMENT,
        "signature": "$SIGNATURE_REPLACEMENT"
      },
      "type": "FCL:VIEW:RESPONSE"
    }
""".trimIndent()

private val FCL_PRE_AUTHZ_RESPONSE = """
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

private val FCL_SIGN_MESSAGE_RESPONSE = """
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

suspend fun fclAuthnResponse(fcl: FclAuthnResponse, address: String): String {
    val accountProofSign = if (!fcl.body.nonce.isNullOrBlank()) {
        hdWallet().signData(fcl.encodeAccountProof(address))
    } else ""

    return FCL_AUTHN_RESPONSE
        .replace(ADDRESS_REPLACEMENT, address)
        .replace(PRE_AUTHZ_REPLACEMENT, generateAuthnPreAuthz())
        .replace(USER_SIGNATURE_REPLACEMENT, if (accountProofSign.isEmpty()) "" else FCL_AUTHN_RESPONSE_USER_SIGNATURE)
        .replace(
            ACCOUNT_PROOF_REPLACEMENT,
            if (accountProofSign.isEmpty()) "" else FCL_AUTHN_RESPONSE_ACCOUNT_PROOF.replace(ADDRESS_REPLACEMENT, address)
                .replace(SIGNATURE_REPLACEMENT, accountProofSign).replace(NONCE_REPLACEMENT, fcl.body.nonce.orEmpty())
        )
}

fun fclPreAuthzResponse(address: String): String {
    return FCL_PRE_AUTHZ_RESPONSE
        .replace(ADDRESS_REPLACEMENT, address)
        .replace(PAYER_ADDRESS_REPLACEMENT, GasConfig.payer().address)
}

fun fclAuthzResponse(address: String, signature: String, keyId: Int? = 0): String {
    return FCL_AUTHZ_RESPONSE
        .replace(ADDRESS_REPLACEMENT, address)
        .replace(SIGNATURE_REPLACEMENT, signature)
        .replace(KEY_ID_REPLACEMENT, "$keyId")
}

fun fclSignMessageResponse(fcl: FclSignMessageResponse, address: String): String {
    val message = fcl.body?.message?.hexToBytes() ?: throw IllegalArgumentException("Message is empty")
    return FCL_SIGN_MESSAGE_RESPONSE
        .replace(ADDRESS_REPLACEMENT, address)
        .replace(SIGNATURE_REPLACEMENT, hdWallet().signData(DomainTag.USER_DOMAIN_TAG + message))
}

private suspend fun generateAuthnPreAuthz(): String {
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

