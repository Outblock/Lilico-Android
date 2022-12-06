package io.outblock.lilico.page.staking.amount.dialog

import android.os.Parcelable
import io.outblock.lilico.manager.staking.StakingProvider
import io.outblock.lilico.page.profile.subpage.currency.model.Currency
import kotlinx.parcelize.Parcelize

@Parcelize
class StakingAmountConfirmModel(
    val amount: Float,
    val coinRate: Float,
    val currency: Currency,
    val rate: Float,
    val rewardCoin: Float,
    val rewardUsd: Float,
    val provider: StakingProvider,
    val isUnstake: Boolean,
) : Parcelable