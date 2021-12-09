package io.outblock.lilico

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.TestWallet.Companion.DERIVATION_PATH
import io.outblock.lilico.TestWallet.Companion.HOST_TESTNET
import io.outblock.lilico.TestWallet.Companion.MNEMONIC
import io.outblock.lilico.TestWallet.Companion.TEST_ADDRESS
import io.outblock.wallet.extensions.toHex
import junit.framework.Assert.assertEquals
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import org.junit.runner.RunWith
import wallet.core.jni.CoinType
import wallet.core.jni.Curve
import wallet.core.jni.HDWallet
import wallet.core.jni.PrivateKey
import java.security.Provider
import java.security.Security


val payerAccountAddress: FlowAddress = FlowAddress("f8d6e0586b0a20c7")

@RunWith(AndroidJUnit4::class)
class TestFlowTransaction {

    init {
        System.loadLibrary("TrustWalletCore");
    }

    @Test
    fun testSimpleTransaction() {
        val wallet = HDWallet(MNEMONIC, "")
        val privateKey = wallet.getCurveKey(Curve.NIST256P1, DERIVATION_PATH)
        val accessApi = Flow.newAccessApi(HOST_TESTNET, 9000)

        assertEquals("638dc9ad0eee91d09249f0fd7c5323a11600e20d5b9105b66b782a96236e74cf", privateKey.data().bytesToHex())
//        updateSecurityProvider()
        val address = FlowAddress("0x4f05d22690e07938")
        val signer = WalletCoreSigner(
            privateKey,
            SignatureAlgorithm.ECDSA_P256,
            HashAlgorithm.SHA2_256)

//        val data = "hello schnorr".encodeToByteArray()
//        val signedData = signer.sign(data)
//        println("=========> transaction --> ${signedData.bytesToHex()}")

        val response = accessApi.simpleFlowTransaction(address = address, signer = signer, keyIndex= 1) {
            script {
                """
                transaction {
                  execute {
                    log("A transaction happened")
                  }
                }
                """
            }
        }.send()
        println("=========> transaction id:${response.transactionId!!.bytes.bytesToHex()}")
    }

    /**
     * 修复Android 上相关加密方式失效问题
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

//    @Test
//    fun testFlowTransaction() {
//        Log.w("method", "testFlowTransaction()")
//        val wallet = HDWallet(MNEMONIC, "")
//
//        val privateKey = wallet.getDerivedKey(CoinType.FLOW, 0, 0, 0)
//        val publicKey = privateKey.publicKeyNist256p1.uncompressed()
//
//        // AccessAPI is sync
//        val accessApi = Flow.newAccessApi(HOST_TESTNET, 9000)
//
//        // Better use async in the future
//        val asyncApi = Flow.newAsyncAccessApi(HOST_TESTNET, 9000)
//
//        val accountAddress = FlowAddress(TEST_ADDRESS)
//        val account = accessApi.getAccountAtLatestBlock(accountAddress)!!
//
//        val keyIndex = 0
//
//        // TODO 定义以及如何获取
//        val signerIndex = 0
//
//        val payerAccountKey = accessApi.getAccountKey(payerAccountAddress, 0)
//
//        // Check this one, better use DSL to construct transaction
//        // https://github.com/the-nft-company/flow-jvm-sdk/blob/main/src/test/kotlin/com/nftco/flow/sdk/TransactionTest.kt#L187-L215
//
//
//        // We need let the privateKey we got from wallet core to confirm Signer Protocol
//        // Then use func like FlowAccessApi.flowTransaction and FlowAccessApi.simpleFlowTransaction to send transaction
//        // https://github.com/the-nft-company/flow-jvm-sdk/blob/8b4f4fb2cf741aff317e08bd14881038a93c24f1/src/main/kotlin/com/nftco/flow/sdk/models.kt#L96-L107
//        // https://github.com/the-nft-company/flow-jvm-sdk/blob/a1e99c41a5948bb15ac2d5c5a74c754c86f57044/src/main/kotlin/com/nftco/flow/sdk/transaction-dsl.kt#L31-L44
//
//        val jvmPrivateKey = Crypto.decodePrivateKey(privateKey.data().bytesToHex(), SignatureAlgorithm.ECDSA_P256)
//        with(TransactionBuilder()) {
//            script = FlowScript(SCRIPT)
//            arguments = mutableListOf(FlowArgument(UFix64NumberField("10")), FlowArgument(AddressField(accountAddress.stringValue)))
//            referenceBlockId = accessApi.getLatestBlockHeader().id
//            gasLimit = 1000
//            proposalKey = FlowTransactionProposalKey(
//                accountAddress,
//                payerAccountKey.id,
//                sequenceNumber = payerAccountKey.sequenceNumber.toLong()
//            )
//            payerAddress = payerAccountAddress
//            authorizers = listOf(accountAddress)
//            payloadSignatures = listOf(
//                PendingSignature(
//                    address = account.address,
//                    keyIndex = keyIndex,
//                    signer = Crypto.getSigner(jvmPrivateKey, HashAlgorithm.SHA2_256)
//                )
//            )
//            envelopeSignatures = listOf(
//                PendingSignature(
//                    address = account.address,
//                    keyIndex = keyIndex,
//                    signer = Crypto.getSigner(jvmPrivateKey, HashAlgorithm.SHA2_256)
//                )
//            )
//        }
////        accessApi.simpleFlowTransaction()
////        accessApi.sendTransaction(
////            FlowTransaction(
////                script = FlowScript(SCRIPT),
////                arguments = listOf(FlowArgument(UFix64NumberField("10")), FlowArgument(AddressField(accountAddress.stringValue))),
////
////            )
////        )
//    }

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