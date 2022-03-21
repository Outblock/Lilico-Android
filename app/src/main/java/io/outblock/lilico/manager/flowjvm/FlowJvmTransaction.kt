package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import io.outblock.lilico.wallet.getPrivateKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

class FlowJvmTransaction {

    fun send(fromAddress: String, toAddress: String, amount: Float): String? {
        logd(TAG, "send > fromAddress:$fromAddress, toAddress:$toAddress, amount:$amount")
        updateSecurityProvider()
        try {
            val latestBlockId = FlowApi.get().getLatestBlockHeader().id

            val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress))!!

            val tx = flowTransaction {
                script { CADENCE_TRANSFER_TOKEN }

                arguments {
                    arg { ufix64(amount) }
                    arg { address(toAddress) }
                }

                referenceBlockId = latestBlockId
                gasLimit = 100

                proposalKey {
                    address = payerAccount.address
                    keyIndex = payerAccount.keys[0].id
                    sequenceNumber = payerAccount.keys[0].sequenceNumber.toLong()
                }

                authorizers(mutableListOf(FlowAddress(fromAddress)))
                payerAddress = payerAccount.address

                signatures {
                    signature {
                        address = payerAccount.address
                        keyIndex = 0
                        signer = Crypto.getSigner(
                            privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                            hashAlgo = HashAlgorithm.SHA2_256
                        )
                    }
                }
            }

            val txID = FlowApi.get().sendTransaction(tx)
            logd(TAG, "send > txID:$${txID.bytes.bytesToHex()}")
            return txID.bytes.bytesToHex()
        } catch (e: Exception) {
            loge(TAG, e)
            return null
        }
    }

    /**
     * fix: java.security.NoSuchAlgorithmException: no such algorithm: ECDSA for provider BC
     */
    private fun updateSecurityProvider() {
        // Web3j will set up the provider lazily when it's first used.
        val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) ?: return
        if (provider.javaClass == BouncyCastleProvider::class.java) {
            // BC with same package name, shouldn't happen in real life.
            return
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }

    companion object {
        private val TAG = FlowJvmTransaction::class.java.simpleName
    }
}