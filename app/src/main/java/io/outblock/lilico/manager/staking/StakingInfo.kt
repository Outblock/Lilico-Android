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