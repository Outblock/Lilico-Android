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

    fun send(fromAddress: String, toAddress: String, amount: Float): FlowTransactionResult? {
        logd(TAG, "send > fromAddress:$fromAddress, toAddress:$toAddress, amount:$amount")
        updateSecurityProvider()
        try {
            val latestBlockId = FlowApi.get().getLatestBlockHeader().id

            val payerAccount = FlowApi.get().getAccountAtLatestBlock(FlowAddress(fromAddress))!!

            val tx = flowTransaction {
                script { SEND_SCRIPT }

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
            val result = waitForSeal(FlowApi.get(), txID, timeoutMs = 30_000).throwOnError()
            logd(TAG, "send > result:$result")
            return result
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

        private val SEND_SCRIPT = """
                    import FungibleToken from 0xFungibleToken
                    import FlowToken from 0xFlowToken
                    
                    transaction(amount: UFix64, to: Address) {
                    
                        // The Vault resource that holds the tokens that are being transferred
                        let sentVault: @FungibleToken.Vault
                    
                        prepare(signer: AuthAccount) {
                    
                            // Get a reference to the signer's stored vault
                            let vaultRef = signer.borrow<&FlowToken.Vault>(from: /storage/flowTokenVault)
                                ?? panic("Could not borrow reference to the owner's Vault!")
                    
                            // Withdraw tokens from the signer's stored vault
                            self.sentVault <- vaultRef.withdraw(amount: amount)
                        }
                    
                        execute {
                    
                            // Get the recipient's public account object
                            let recipient = getAccount(to)
                    
                            // Get a reference to the recipient's Receiver
                            let receiverRef = recipient.getCapability(/public/flowTokenReceiver)
                                .borrow<&{FungibleToken.Receiver}>()
                                ?? panic("Could not borrow receiver reference to the recipient's Vault")
                    
                            // Deposit the withdrawn tokens in the recipient's receiver
                            receiverRef.deposit(from: <-self.sentVault)
                        }
                    }
                """
    }
}