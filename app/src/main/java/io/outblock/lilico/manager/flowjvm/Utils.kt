package io.outblock.lilico.manager.flowjvm

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.Field
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.flowjvm.model.FlowBoolListResult
import io.outblock.lilico.manager.flowjvm.transaction.AsArgument
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.loge


internal fun FlowScriptResponse.parseSearchAddress(): String? {
    // {"type":"Optional","value":{"type":"Address","value":"0x5d2cd5bf303468fa"}}
    return try {
        val json = Gson().fromJson<Map<String, Any>>(String(bytes), object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as Map<*, *>)["value"].toString()
    } catch (e: Exception) {
        loge(e, report = false)
        return null
    }
}

internal fun FlowScriptResponse.parseBool(default: Boolean = false): Boolean? {
    // {"type":"Bool","value":false}
    return try {
        val json = Gson().fromJson<Map<String, Any>>(String(bytes), object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as? Boolean) ?: default
    } catch (e: Exception) {
        loge(e, report = false)
        return default
    }
}

internal fun FlowScriptResponse.parseBoolList(): List<Boolean>? {
    // {"type":"Array","value":[{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":false}]}
    return try {
        val result = Gson().fromJson(String(bytes), FlowBoolListResult::class.java)
        return result.value.map { it.value }
    } catch (e: Exception) {
        loge(e, report = false)
        null
    }
}

internal fun FlowScriptResponse?.parseFloat(default: Float = 0f): Float {
    // {"type":"UFix64","value":"12.34"}
    this ?: return default
    return try {
        val json = Gson().fromJson<Map<String, String>>(String(bytes), object : TypeToken<Map<String, String>>() {}.type)
        (json["value"]?.toFloatOrNull()) ?: default
    } catch (e: Exception) {
        loge(e, report = false)
        return default
    }
}

fun addressVerify(address: String): Boolean {
    if (!address.startsWith("0x")) {
        return false
    }
    return try {
        FlowApi.get().getAccountAtLatestBlock(FlowAddress(address)) != null
    } catch (e: Exception) {
        loge(e, report = false)
        false
    }
}

fun Nft.formatCadence(script: String): String {
    val config = NftCollectionConfig.get(contract.address) ?: return script
    return config.formatCadence(script)
}

fun NftCollection.formatCadence(script: String): String {
    return script.replace("<NFT>", contractName)
        .replace("<NFTAddress>", address)
        .replace("<CollectionStoragePath>", path.storagePath)
        .replace("<CollectionPublic>", path.publicCollectionName)
        .replace("<CollectionPublicPath>", path.publicPath)
        .replace("<Token>", contractName)
        .replace("<TokenAddress>", address)
        .replace("<TokenCollectionStoragePath>", path.storagePath)
        .replace("<TokenCollectionPublic>", path.publicCollectionName)
        .replace("<TokenCollectionPublicPath>", path.publicPath)
}

class CadenceArgumentsBuilder {
    private var _values: MutableList<Field<*>> = mutableListOf()

    fun arg(arg: Field<*>) = _values.add(arg)

    fun arg(builder: JsonCadenceBuilder.() -> Field<*>) = arg(builder(JsonCadenceBuilder()))

    fun build(): MutableList<Field<*>> = _values

    fun toFlowArguments(): FlowArgumentsBuilder.() -> Unit {
        return {
            _values.forEach { arg(it) }
        }
    }
}

fun (CadenceArgumentsBuilder.() -> Unit).builder(): CadenceArgumentsBuilder {
    val argsBuilder = CadenceArgumentsBuilder()
    this(argsBuilder)
    return argsBuilder
}

@WorkerThread
fun FlowAddress.lastBlockAccount(): FlowAccount? {
    return FlowApi.get().getAccountAtLatestBlock(this)
}

@WorkerThread
fun FlowAddress.lastBlockAccountKeyId(): Int {
    return lastBlockAccount()?.keys?.firstOrNull()?.id ?: 0
}

fun Field<*>.valueString(): String = if (value is String) value as String else Flow.OBJECT_MAPPER.writeValueAsString(value)

fun FlowArgument.toAsArgument(): AsArgument {
    with(jsonCadence) {
        return AsArgument(
            type = type,
            value = when (value) {
                is Array<*> -> (value as Array<*>).map { (it as? Field<*>)?.toObj() ?: it.toString() }
                is String -> value as String
                else -> valueToObj()
            },
        )
    }
}

private fun Field<*>.toObj(): Any {
    if (value is String) return mapOf("type" to type, "value" to value as String)

    val json = Flow.OBJECT_MAPPER.writeValueAsString(value)
    return runCatching {
        Gson().fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
    }.getOrNull() ?: json
}

private fun Field<*>.valueToObj(): Any {
    val json = Flow.OBJECT_MAPPER.writeValueAsString(value)
    return runCatching {
        Gson().fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
    }.getOrNull() ?: json
}
