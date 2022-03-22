package io.outblock.lilico.manager.flowjvm

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowScriptResponse
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

internal fun FlowScriptResponse?.parseFloat(default: Float = 0f): Float {
    // {"type":"Bool","value":12.34}
    this ?: return default
    return try {
        val json = Gson().fromJson<Map<String, Any>>(String(bytes), object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as? Float) ?: default
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