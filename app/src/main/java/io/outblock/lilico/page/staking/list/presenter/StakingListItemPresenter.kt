package io.outblock.lilico.page.staking.list.presenter

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemStakeListBinding
import io.outblock.lilico.manager.staking.*
import io.outblock.lilico.page.staking.amount.StakingAmountActivity
import io.outblock.lilico.page.staking.list.model.StakingListItemModel
import io.outblock.lilico.utils.extensions.toSafeFloat
import io.outblock.lilico.utils.extensions.toSafeInt
import io.outblock.lilico.utils.formatNum

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


class StakingListItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<StakingListItemModel> {

    private val binding by lazy { ItemStakeListBinding.bind(view) }

    @SuppressLint("SetTextI18n")
    override fun bind(model: StakingListItemModel) {
        with(binding) {
            claimButton.setOnClickListener { StakingAmountActivity.launch(view.context, model.provider) }
            Glide.with(providerIcon).load(model.provider.icon).placeholder(R.drawable.placeholder).into(providerIcon)
            providerName.text = model.provider.name
            providerRate.text = (model.provider.rate() * 100).formatNum(2) + "%"
            amountView.text = model.stakingNode.stakingCount().formatNum(3)
            rewardView.text = model.stakingNode.tokensRewarded.formatNum(3)
        }

        view.setOnClickListener { }
    }

}