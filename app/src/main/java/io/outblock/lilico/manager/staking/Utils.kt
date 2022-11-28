package io.outblock.lilico.manager.staking

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.utils.extensions.toSafeFloat
import io.outblock.lilico.utils.extensions.toSafeInt

fun parseStakingInfoResult(json: String?): StakingInfo? {
    json ?: return null
    val info = Gson().fromJson(json, StakingInfoInner::class.java)
    return StakingInfo(
        nodes = info.value?.map { value ->
            StakingNode(
                delegatorId = value.getByName("id").toSafeInt(),
                nodeID = value.getByName("nodeID").orEmpty(),
                tokensCommitted = value.getByName("tokensCommitted").toSafeFloat(),
                tokensStaked = value.getByName("tokensStaked").toSafeFloat(),
                tokensUnstaking = value.getByName("tokensUnstaking").toSafeFloat(),
                tokensRewarded = value.getByName("tokensRewarded").toSafeFloat(),
                tokensUnstaked = value.getByName("tokensUnstaked").toSafeFloat(),
                tokensRequestedToUnstake = value.getByName("tokensRequestedToUnstake").toSafeFloat(),
            )
        }.orEmpty()
    )
}

fun parseStakingDelegatorInfo(json: String?): Map<String, Int> {
    json ?: return emptyMap()
    val info = Gson().fromJson(json, StakingDelegatorInner::class.java)
    return info.value?.value?.associate { it?.key?.value.orEmpty() to (it?.value?.value?.firstOrNull()?.key?.value?.toSafeInt() ?: 0) }.orEmpty()
}

data class StakingInfoInner(
    @SerializedName("type")
    val type: String?,
    @SerializedName("value")
    val value: List<Value?>?
) {
    data class Value(
        @SerializedName("type")
        val type: String?,
        @SerializedName("value")
        val value: Value1?
    ) {

        data class Value1(
            @SerializedName("fields")
            val fields: List<Field?>?,
            @SerializedName("id")
            val id: String?
        ) {
            data class Field(
                @SerializedName("name")
                val name: String?,
                @SerializedName("value")
                val value: Value2?
            ) {
                data class Value2(
                    @SerializedName("type")
                    val type: String?,
                    @SerializedName("value")
                    val value: String?
                )
            }
        }
    }
}

fun StakingInfoInner.Value?.getByName(name: String): String? = this?.value?.fields?.firstOrNull { it?.name == name }?.value?.value

internal data class StakingDelegatorInner(
    @SerializedName("type")
    val type: String?,
    @SerializedName("value")
    val value: Value1?
) {
    internal data class Value1(
        @SerializedName("type")
        val type: String?,
        @SerializedName("value")
        val value: List<Value2?>?
    ) {
        internal data class Value2(
            @SerializedName("key")
            val key: Key?,
            @SerializedName("value")
            val value: Value?
        ) {
            internal data class Key(
                @SerializedName("type")
                val type: String?,
                @SerializedName("value")
                val value: String?
            )

            internal data class Value(
                @SerializedName("type")
                val type: String?,
                @SerializedName("value")
                val value: List<Value3?>?
            ) {
                internal data class Value3(
                    @SerializedName("key")
                    val key: Key?
                ) {
                    internal data class Key(
                        @SerializedName("type")
                        val type: String?,
                        @SerializedName("value")
                        val value: String?
                    )
                }
            }
        }
    }
}
