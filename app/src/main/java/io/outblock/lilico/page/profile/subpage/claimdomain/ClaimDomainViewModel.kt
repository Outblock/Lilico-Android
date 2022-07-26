package io.outblock.lilico.page.profile.subpage.claimdomain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.TYPE_STRING
import com.nftco.flow.sdk.crypto.Crypto
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.flowjvm.transaction.*
import io.outblock.lilico.manager.transaction.TransactionState
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.ClaimDomainPrepare
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.window.bubble.tools.pushBubbleStack
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.wallet.getPrivateKey
import io.outblock.lilico.wallet.toAddress

class ClaimDomainViewModel : ViewModel() {

    val usernameLiveData = MutableLiveData<String>()
    val claimTransactionIdLiveData = MutableLiveData<String?>()

    fun load() {
        viewModelIOScope(this) {
            userInfoCache().read()?.username?.let { usernameLiveData.postValue(it) }
        }
    }

    fun claim() {
        viewModelIOScope(this) {
            try {
                assert(usernameLiveData.value != null) { "username is null" }
                val prepare = retrofit().create(ApiService::class.java).claimDomainPrepare().data!!
                val signable = buildPayerSignable(prepare)
                val transaction = retrofit().create(ApiService::class.java).claimDomainSignature(signable).data!!
                watchTransactionState(transaction.txId!!)
                claimTransactionIdLiveData.postValue(transaction.txId)
            } catch (e: Exception) {
                e.printStackTrace()
                claimTransactionIdLiveData.postValue(null)
            }
        }
    }

    private fun buildPayerSignable(prepare: ClaimDomainPrepare): PayerSignable {
        updateSecurityProvider()
        val walletAddress = walletCache().read()?.primaryWalletAddress().orEmpty().toAddress()
        val account = FlowApi.get().getAccountAtLatestBlock(FlowAddress(walletAddress))
            ?: throw RuntimeException("get wallet account error")
        return flowTransaction {
            script { prepare.cadence!! }

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", TYPE_STRING)
            jsonObject.addProperty("value", usernameLiveData.value!!)
            arguments = mutableListOf(FlowArgument(jsonObject.toString().toByteArray()))

            referenceBlockId = FlowApi.get().getLatestBlockHeader().id

            gasLimit = 9999

            proposalKey {
                address = FlowAddress(walletAddress)
                keyIndex = account.keys.first().id
                sequenceNumber = account.keys.first().sequenceNumber
            }

            authorizers(listOf(walletAddress, prepare.lilicoServerAddress!!, prepare.flownsServerAddress!!).map { FlowAddress(it) }.toMutableList())

            payerAddress = FlowAddress(GasConfig.payer().address)

            addPayloadSignatures {
                signature(
                    FlowAddress(walletAddress),
                    account.keys.first().id,
                    Crypto.getSigner(
                        privateKey = Crypto.decodePrivateKey(getPrivateKey(), SignatureAlgorithm.ECDSA_SECP256k1),
                        hashAlgo = HashAlgorithm.SHA2_256
                    ),
                )
            }
        }.buildPayerSignable()
    }

    private fun FlowTransaction.buildPayerSignable(): PayerSignable {
        val voucher = Voucher(
            cadence = script.stringValue,
            refBlock = referenceBlockId.base16Value,
            computeLimit = gasLimit.toInt(),
            arguments = arguments.map { it.jsonCadence }.map { AsArgument(it.type, it.value.toString()) },
            proposalKey = ProposalKey(
                address = proposalKey.address.base16Value.toAddress(),
                keyId = proposalKey.keyIndex,
                sequenceNum = proposalKey.sequenceNumber.toInt(),
            ),
            payer = payerAddress.base16Value.toAddress(),
            authorizers = authorizers.map { it.base16Value.toAddress() },
            payloadSigs = payloadSignatures.map {
                Singature(
                    address = it.address.base16Value.toAddress(),
                    keyId = it.keyIndex,
                    sig = it.signature.base16Value,
                )
            },
        )

        return PayerSignable(
            transaction = voucher,
            message = PayerSignable.Message(encodeTransactionPayload())
        )
    }

    private fun watchTransactionState(txId: String) {
        val transactionState = TransactionState(
            transactionId = txId,
            time = System.currentTimeMillis(),
            state = FlowTransactionStatus.UNKNOWN.num,
            type = TransactionState.TYPE_CLAIM_DOMAIN,
            data = "",
        )
        uiScope {
            if (TransactionStateManager.getTransactionStateById(txId) != null) return@uiScope
            TransactionStateManager.newTransaction(transactionState)
            pushBubbleStack(transactionState)
        }
    }
}