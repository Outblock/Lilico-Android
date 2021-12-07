package io.outblock.lilico

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.AddressField
import com.nftco.flow.sdk.cadence.UFix64NumberField
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.TestWallet.Companion.HOST_TESTNET
import io.outblock.lilico.TestWallet.Companion.MNEMONIC
import io.outblock.lilico.TestWallet.Companion.PRIVATE_KEY
import org.junit.Test
import org.junit.runner.RunWith
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet

val payerAccountAddress: FlowAddress = FlowAddress("f8d6e0586b0a20c7")

@RunWith(AndroidJUnit4::class)
class TestFlowTransaction {

    @Test
    fun testFlowTransaction() {
        Log.w("method", "testFlowTransaction()")
        val wallet = HDWallet(MNEMONIC, "")
        val accessApi = Flow.newAccessApi(HOST_TESTNET, 9000)
        // TODO 如何从 wallet-core 的 key 获取 flow 的 address
        val accountAddress = FlowAddress("TODO")

        val account = accessApi.getAccountAtLatestBlock(FlowAddress(wallet.getAddressForCoin(CoinType.FLOW)))!!
        val keyIndex = account.keys.indexOfFirst { it.publicKey.bytes.contentEquals(wallet.getKeyForCoin(CoinType.FLOW).publicKeyNist256p1.data()) }
        val publicKey = wallet.getKeyForCoin(CoinType.FLOW).publicKeyNist256p1

        // TODO 如何根据 address 取到 privateKey
        val privateKey = Crypto.decodePrivateKey(PRIVATE_KEY)

        // TODO 定义以及如何获取
        val signerIndex = 0

        val payerAccountKey = accessApi.getAccountKey(payerAccountAddress, 0)

        accessApi.sendTransaction(
            FlowTransaction(
                script = FlowScript(SCRIPT),
                arguments = listOf(FlowArgument(UFix64NumberField("10")), FlowArgument(AddressField(accountAddress.stringValue))),
                referenceBlockId = accessApi.getLatestBlockHeader().id,
                gasLimit = 1000,
                proposalKey = FlowTransactionProposalKey(
                    accountAddress,
                    payerAccountKey.id,
                    sequenceNumber = payerAccountKey.sequenceNumber.toLong()
                ),
                payerAddress = payerAccountAddress,
                authorizers = listOf(accountAddress),
                payloadSignatures = listOf(
                    FlowTransactionSignature(
                        address = accountAddress,
                        signerIndex = signerIndex,
                        keyIndex = keyIndex,
                        signature = FlowSignature(privateKey.hex)
                    )
                ),
                envelopeSignatures = listOf(
                    FlowTransactionSignature(
                        address = payerAccountAddress,
                        signerIndex = signerIndex,
                        keyIndex = keyIndex,
                        signature = FlowSignature(privateKey.hex)
                    )
                ),
            )
        )

//        val keyPair = Crypto.generateKeyPair(SignatureAlgorithm.ECDSA_SECP256k1)
//        accessApi.simpleFlowTransaction(
//            address = FlowAddress(wallet.getAddressForCoin(CoinType.FLOW)),
//            signer = Crypto.getSigner(keyPair.private, HashAlgorithm.SHA2_256),
//            gasLimit = 1000,
//            keyIndex = 0,
//            block = {
//                Log.w("block", "simpleFlowTransaction() block callback")
//            }
//        )
    }

    private fun FlowAccessApi.getAccountKey(address: FlowAddress, keyIndex: Int): FlowAccountKey {
        return getAccountAtLatestBlock(address)!!.keys[keyIndex]
    }

    companion object {
        private const val SCRIPT = """
import FungibleToken from 0xFUNGIBLE_TOKEN_ADDRESS
import FlowToken from 0xFLOW_TOKEN_ADDRESS

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