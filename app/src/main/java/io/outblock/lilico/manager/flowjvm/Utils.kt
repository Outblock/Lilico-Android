package io.outblock.lilico.manager.flowjvm

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowScriptResponse
import io.outblock.lilico.manager.config.NftCollection
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.flowjvm.model.FlowBoolListResult
import io.outblock.lilico.network.model.Nft
import io.outblock.lilico.utils.loge


internal fun FlowScriptResponse.parseSearchAddress(): String? {
    // {"type":"Optional","value":{"type":"Address","value":"0x5d2cd5bf303468fa"}}
    return try {
        val json = Gson().fromJson<Map<String, Any>>(String(bytes), object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as Map<*, *>)["value"].toString()
    } catch (e: Exception) {
        loge(e)
        return null
    }
}

internal fun FlowScriptResponse.parseBool(default: Boolean = false): Boolean? {
    // {"type":"Bool","value":false}
    return try {
        val json = Gson().fromJson<Map<String, Any>>(String(bytes), object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as? Boolean) ?: default
    } catch (e: Exception) {
        loge(e)
        return default
    }
}

internal fun FlowScriptResponse.parseBoolList(): List<Boolean>? {
    // {"type":"Array","value":[{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":false}]}
    return try {
        val result = Gson().fromJson(String(bytes), FlowBoolListResult::class.java)
        return result.value.map { it.value }
    } catch (e: Exception) {
        loge(e)
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
        loge(e)
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
        loge(e)
        false
    }
}

fun Nft.formatCadence(script: String): String {
    val config = NftCollectionConfig.get(contract.address) ?: return script
    return config.formatCadence(script)
}

fun NftCollection.formatCadence(script: String): String {
    return script.replace("<NFT>", contractName)
        .replace("<NFTAddress>", address(forceMainnet = false))
        .replace("<CollectionStoragePath>", path.storagePath)
        .replace("<CollectionPublic>", path.publicCollectionName)
        .replace("<CollectionPublicPath>", path.publicPath)
        .replace("<Token>", contractName)
        .replace("<TokenAddress>", address())
        .replace("<TokenCollectionStoragePath>", path.storagePath)
        .replace("<TokenCollectionPublic>", path.publicCollectionName)
        .replace("<TokenCollectionPublicPath>", path.publicPath)
}