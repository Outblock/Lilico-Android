package io.outblock.lilico.manager.flowjvm.transaction

import com.nftco.flow.sdk.FlowArgumentsBuilder
import com.nftco.flow.sdk.cadence.Field
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder

class FlowTransactionBuilder {
    private var _values: MutableList<Field<*>> = mutableListOf()

    fun arg(arg: Field<*>) = _values.add(arg)

    fun arg(arg: JsonCadenceBuilder.() -> Field<*>) = arg(arg(JsonCadenceBuilder()))

    fun build(): MutableList<Field<*>> = _values

    fun toFlowArguments(): FlowArgumentsBuilder.() -> Unit {
        return {
            _values.forEach { arg(it) }
        }
    }
}