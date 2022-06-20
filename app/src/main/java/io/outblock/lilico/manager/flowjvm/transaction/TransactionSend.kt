package io.outblock.lilico.manager.flowjvm.transaction

import com.nftco.flow.sdk.cadence.Field
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.config.GasConfig
import io.outblock.lilico.manager.flowjvm.transaction.resolve.AccountsResolver
import io.outblock.lilico.manager.flowjvm.transaction.resolve.CadenceResolver
import io.outblock.lilico.manager.flowjvm.transaction.resolve.RefBlockResolver
import io.outblock.lilico.manager.flowjvm.transaction.resolve.SequenceNumberResolver

suspend fun sendTransaction(builder: ScriptBuilder.() -> Unit): String {
    val ix = prepare(ScriptBuilder().apply { builder(this) })
    listOf(
        CadenceResolver(),
        AccountsResolver(),
        RefBlockResolver(),
        SequenceNumberResolver(),
//        SignatureResolver(),
    ).forEach { it.resolve(ix) }
    return ""
}

private fun prepare(builder: ScriptBuilder): Interaction {
    return Interaction().apply {
        builder.script?.let {
            tag = Interaction.Tag.transaction.value
            message.cadence = it
        }
        builder.arguments.map { it.toFclArgument() }.apply {
            message.arguments = map { it.tempId }
            arguments = toLinkedMap()
        }

        builder.limit?.let { message.computeLimit = it }

        account = Account(builder.walletAddress ?: walletCache().read()?.primaryWalletAddress())

        payer = builder.payer ?: (if (GasConfig.isFreeGas()) GasConfig.payer().address else account.addr)

        proposer = account.addr
    }
}

private fun List<Argument>.toLinkedMap(): LinkedHashMap<String, Argument> {
    val map = linkedMapOf<String, Argument>()
    forEach { map[it.tempId] = it }
    return map
}

class ScriptBuilder {

    internal var script: String? = null

    internal var walletAddress: String? = null

    internal var payer: String? = null

    internal var arguments: MutableList<Field<*>> = mutableListOf()

    internal var limit: Int? = 9999

    fun script(script: String) {
        this.script = script
    }

    fun arguments(arguments: MutableList<Field<*>>) {
        this.arguments = arguments
    }

    fun arguments(arguments: JsonCadenceBuilder.() -> Iterable<Field<*>>) {
        val builder = JsonCadenceBuilder()
        this.arguments = arguments(builder).toMutableList()
    }

    fun arg(argument: Field<*>) = arguments.add(argument)

    fun arg(argument: JsonCadenceBuilder.() -> Field<*>) = arg(argument(JsonCadenceBuilder()))

    fun gaslimit(limit: Int) {
        this.limit = limit
    }

    fun walletAddress(address: String) {
        this.walletAddress = address
    }

    fun payer(payerAddress: String) {
        this.payer = payerAddress
    }
}