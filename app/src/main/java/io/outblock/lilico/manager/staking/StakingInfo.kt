package io.outblock.lilico.manager.staking


import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.utils.extensions.toSafeFloat
import io.outblock.lilico.utils.extensions.toSafeInt


fun parseStakingInfoResult(json: String?): StakingInfo? {
    json ?: return null
    val info = Gson().fromJson(json, StakingInfoInner::class.java)
    return StakingInfo(
        delegatorId = info.getByName("id").toSafeInt(),
        nodeID = info.getByName("nodeID").toSafeFloat(),
        tokensCommitted = info.getByName("tokensCommitted").toSafeFloat(),
        tokensStaked = info.getByName("tokensStaked").toSafeFloat(),
        tokensUnstaking = info.getByName("tokensUnstaking").toSafeFloat(),
        tokensRewarded = info.getByName("tokensRewarded").toSafeFloat(),
        tokensUnstaked = info.getByName("tokensUnstaked").toSafeFloat(),
        tokensRequestedToUnstake = info.getByName("tokensRequestedToUnstake").toSafeFloat(),
    )
}

private data class StakingInfoInner(
    @SerializedName("type")
    val type: String?,
    @SerializedName("value")
    val value: List<Value?>?
) {
    data class Value(
        @SerializedName("type")
        val type: String?,
        @SerializedName("value")
        val value: Value?
    ) {
        data class Value(
            @SerializedName("fields")
            val fields: List<Field?>?,
            @SerializedName("id")
            val id: String?
        ) {
            data class Field(
                @SerializedName("name")
                val name: String?,
                @SerializedName("value")
                val value: Value?
            ) {
                data class Value(
                    @SerializedName("type")
                    val type: String?,
                    @SerializedName("value")
                    val value: String?
                )
            }
        }
    }

    fun value() = value?.firstOrNull()?.value

    fun id() = value()?.id

    fun delegatorId() = getByName("id")

    fun getByName(name: String): String? = value()?.fields?.firstOrNull { it?.name == name }?.value?.value
}