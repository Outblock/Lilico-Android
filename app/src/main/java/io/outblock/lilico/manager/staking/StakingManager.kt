package io.outblock.lilico.manager.staking

import com.google.gson.Gson
import io.outblock.lilico.cache.stakingCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.*
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.safeRun
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal

private const val DEFAULT_APY = 0.093f
private const val TAG = "StakingManager"

object StakingManager {

    private var stakingInfo: StakingInfo? = null
    private var apy = DEFAULT_APY

    private val providers = StakingProviders().apply { refresh() }

    fun init() {
        ioScope {
            val cache = stakingCache().read()
            stakingInfo = cache?.info
            apy = cache?.apy ?: apy
            refresh()
        }
    }

    fun providers() = providers.get()

    fun apy() = apy

    fun isStaked(): Boolean {
        if (stakingInfo == null) {
            refresh()
        }
        return stakingInfo?.aaa != null
    }

    private fun refresh() {
        if (stakingInfo?.delegatorId.isNullOrBlank()) {
            setupStaking()
            logd(TAG, "setupStaking finish")
            createStakingDelegatorId()
        }
        stakingInfo = queryStakingInfo()
        stakingInfo?.let { cache() }

        queryStakingApy()?.let {
            apy = it
            cache()
        }
    }

    private fun cache() {
        ioScope {
            stakingCache().cache(StakingCache(info = stakingInfo, apy = apy))
        }
    }
}

private fun queryStakingInfo(): StakingInfo? {
    val address = walletCache().read()?.primaryWalletAddress() ?: return null

    return runCatching {
        val response = CADENCE_QUERY_STAKE_INFO.executeCadence {
            arg { address(address) }
        }
        val text = String(response!!.bytes)
        logd(TAG, "queryStakingInfo response:$text")
        Gson().fromJson(text, StakingInfo::class.java)
    }.getOrNull()
}

private fun queryStakingApy(): Float? {
    return runCatching {
        val response = CADENCE_GET_STAKE_APY_BY_WEEK.executeCadence {}
        val apy = response?.parseFloat()
        logd(TAG, "queryStakingApy apy:$apy")
        if (apy == 0.0f) null else apy
    }.getOrNull()
}

private fun createStakingDelegatorId() {
    ioScope {
        val provider = StakingManager.providers().firstOrNull { it.isLilico() } ?: return@ioScope
        CADENCE_CREATE_STAKE_DELEGATOR_ID.transactionByMainWallet {
            arg { string(provider.id) }
            arg { ufix64Safe(BigDecimal(0.001)) }
        }
    }
}

private fun setupStaking() {
    safeRun {
        runBlocking { CADENCE_SETUP_STAKING.transactionByMainWallet {} }
    }
}

data class StakingInfo(
    val aaa: String? = null,
    val delegatorId: String? = null,
)

data class StakingCache(
    val info: StakingInfo? = null,
    val apy: Float = DEFAULT_APY,
)