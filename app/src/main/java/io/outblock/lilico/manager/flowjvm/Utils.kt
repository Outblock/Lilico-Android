package io.outblock.lilico.manager.flowjvm

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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