package io.outblock.lilico.manager.flowjvm.transaction


import com.google.gson.annotations.SerializedName

data class Signable(
    @SerializedName("message")
    var message: Message? = null,
    @SerializedName("transaction")
    val transaction: Transaction
) {
    data class Transaction(
        @SerializedName("arguments")
        val arguments: List<Argument>,
        @SerializedName("authorizers")
        val authorizers: List<String>,
        @SerializedName("cadence")
        val cadence: String,
        @SerializedName("computeLimit")
        val computeLimit: Int,
        @SerializedName("envelopeSigs")
        val envelopeSigs: List<Sig>,
        @SerializedName("payer")
        val payer: String,
        @SerializedName("payloadSigs")
        var payloadSigs: List<Sig>? = null,
        @SerializedName("proposalKey")
        val proposalKey: ProposalKey,
        @SerializedName("refBlock")
        val refBlock: String
    ) {
        data class Argument(
            @SerializedName("type")
            val type: String,
            @SerializedName("value")
            val value: String
        )

        data class Sig(
            @SerializedName("address")
            val address: String,
            @SerializedName("keyId")
            val keyId: Int,
            @SerializedName("sig")
            val sig: String? = null,
        )

        data class ProposalKey(
            @SerializedName("address")
            val address: String,
            @SerializedName("keyId")
            val keyId: Int,
            @SerializedName("sequenceNum")
            val sequenceNum: Int
        )
    }

    data class Message(
        @SerializedName("envelope_message")
        val envelopeMessage: String
    )
}