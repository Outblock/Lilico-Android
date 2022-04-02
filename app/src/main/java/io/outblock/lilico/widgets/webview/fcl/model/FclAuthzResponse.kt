package io.outblock.lilico.widgets.webview.fcl.model


import com.google.gson.annotations.SerializedName

data class FclAuthzResponse(
    @SerializedName("body")
    val body: Body,
    @SerializedName("config")
    val config: Config,
    @SerializedName("fclVersion")
    val fclVersion: String,
    @SerializedName("service")
    val service: Service,
    @SerializedName("type")
    val type: String
) {
    data class Body(
        @SerializedName("addr")
        val addr: String,
        @SerializedName("args")
        val args: List<Any>,
        @SerializedName("cadence")
        val cadence: String,
        @SerializedName("data")
        val `data`: Data,
        @SerializedName("f_type")
        val fType: String,
        @SerializedName("f_vsn")
        val fVsn: String,
        @SerializedName("interaction")
        val interaction: Interaction,
        @SerializedName("keyId")
        val keyId: Int,
        @SerializedName("message")
        val message: String,
        @SerializedName("roles")
        val roles: Roles,
        @SerializedName("voucher")
        val voucher: Voucher
    ) {
        class Data

        data class Interaction(
            @SerializedName("account")
            val account: Account,
            @SerializedName("accounts")
            val accounts: Accounts,
            @SerializedName("arguments")
            val arguments: Arguments,
            @SerializedName("assigns")
            val assigns: Assigns,
            @SerializedName("authorizations")
            val authorizations: List<String>,
            @SerializedName("block")
            val block: Block,
            @SerializedName("collection")
            val collection: Collection,
            @SerializedName("events")
            val events: Events,
            @SerializedName("message")
            val message: Message,
            @SerializedName("params")
            val params: Params,
            @SerializedName("payer")
            val payer: String,
            @SerializedName("proposer")
            val proposer: String,
            @SerializedName("reason")
            val reason: Any,
            @SerializedName("status")
            val status: String,
            @SerializedName("tag")
            val tag: String,
            @SerializedName("transaction")
            val transaction: Transaction
        ) {
            data class Account(
                @SerializedName("addr")
                val addr: Any
            )

            data class Accounts(
                @SerializedName("1cad531bae1580f5-0")
                val cad531bae1580f50: Cad531bae1580f50
            ) {
                data class Cad531bae1580f50(
                    @SerializedName("addr")
                    val addr: String,
                    @SerializedName("keyId")
                    val keyId: Int,
                    @SerializedName("kind")
                    val kind: String,
                    @SerializedName("resolve")
                    val resolve: Any,
                    @SerializedName("role")
                    val role: Role,
                    @SerializedName("sequenceNum")
                    val sequenceNum: Int,
                    @SerializedName("signature")
                    val signature: Any,
                    @SerializedName("tempId")
                    val tempId: String
                ) {
                    data class Role(
                        @SerializedName("authorizer")
                        val authorizer: Boolean,
                        @SerializedName("param")
                        val `param`: Boolean,
                        @SerializedName("payer")
                        val payer: Boolean,
                        @SerializedName("proposer")
                        val proposer: Boolean
                    )
                }
            }

            class Arguments

            class Assigns

            data class Block(
                @SerializedName("height")
                val height: Any,
                @SerializedName("id")
                val id: Any,
                @SerializedName("isSealed")
                val isSealed: Any
            )

            data class Collection(
                @SerializedName("id")
                val id: Any
            )

            data class Events(
                @SerializedName("blockIds")
                val blockIds: List<Any>,
                @SerializedName("end")
                val end: Any,
                @SerializedName("eventType")
                val eventType: Any,
                @SerializedName("start")
                val start: Any
            )

            data class Message(
                @SerializedName("arguments")
                val arguments: List<Any>,
                @SerializedName("authorizations")
                val authorizations: List<Any>,
                @SerializedName("cadence")
                val cadence: String,
                @SerializedName("computeLimit")
                val computeLimit: Int,
                @SerializedName("params")
                val params: List<Any>,
                @SerializedName("payer")
                val payer: Any,
                @SerializedName("proposer")
                val proposer: Any,
                @SerializedName("refBlock")
                val refBlock: String
            )

            class Params

            data class Transaction(
                @SerializedName("id")
                val id: Any
            )
        }

        data class Roles(
            @SerializedName("authorizer")
            val authorizer: Boolean,
            @SerializedName("param")
            val `param`: Boolean,
            @SerializedName("payer")
            val payer: Boolean,
            @SerializedName("proposer")
            val proposer: Boolean
        )

        data class Voucher(
            @SerializedName("arguments")
            val arguments: List<Any>,
            @SerializedName("authorizers")
            val authorizers: List<String>,
            @SerializedName("cadence")
            val cadence: String,
            @SerializedName("computeLimit")
            val computeLimit: Int,
            @SerializedName("envelopeSigs")
            val envelopeSigs: List<EnvelopeSig>,
            @SerializedName("payer")
            val payer: String,
            @SerializedName("payloadSigs")
            val payloadSigs: List<Any>,
            @SerializedName("proposalKey")
            val proposalKey: ProposalKey,
            @SerializedName("refBlock")
            val refBlock: String
        ) {
            data class EnvelopeSig(
                @SerializedName("address")
                val address: String,
                @SerializedName("keyId")
                val keyId: Int,
                @SerializedName("sig")
                val sig: Any
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
    }

    data class Config(
        @SerializedName("app")
        val app: App,
        @SerializedName("client")
        val client: Client,
        @SerializedName("services")
        val services: Services
    ) {
        class App

        data class Client(
            @SerializedName("fclLibrary")
            val fclLibrary: String,
            @SerializedName("fclVersion")
            val fclVersion: String,
            @SerializedName("hostname")
            val hostname: String
        )

        class Services
    }

    data class Service(
        @SerializedName("type")
        val type: String
    )
}