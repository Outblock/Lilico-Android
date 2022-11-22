package io.outblock.lilico.manager.staking

import io.outblock.lilico.cache.stakingCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.*
import io.outblock.lilico.manager.transaction.TransactionStateWatcher
import io.outblock.lilico.manager.transaction.isExecuteFinished
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val DEFAULT_APY = 0.093f
const val STAKING_DEFAULT_NORMAL_APY = 0.08f
private const val TAG = "StakingManager"

object StakingManager {

    private var stakingInfo = StakingInfo()
    private var apy = DEFAULT_APY

    private val providers = StakingProviders().apply { refresh() }

    fun init() {
        ioScope {
            val cache = stakingCache().read()
            stakingInfo = cache?.info ?: StakingInfo()
            apy = cache?.apy ?: apy
        }
    }

    fun stakingInfo() = stakingInfo

    fun providers() = providers.get()

    fun apy() = apy

    fun isStaked(): Boolean {
        if (stakingInfo.delegatorId == null) {
            refresh()
        }
        return stakingCount() > 0.0f
    }

    fun stakingCount() = stakingInfo.tokensCommitted + stakingInfo.tokensStaked

    fun refresh() {
        ioScope {
            updateApy()
            if (stakingInfo.delegatorId == null) {
                prepare()
            }
            stakingInfo = queryStakingInfo() ?: StakingInfo()
            cache()
        }
    }

    private fun updateApy() {
        queryStakingApy()?.let {
            apy = it
            cache()
        }
    }

    private suspend fun prepare() = suspendCoroutine { continuation ->
        runCatching {
            runBlocking {
                setupStaking {
                    runBlocking { createStakingDelegatorId() }
                    continuation.resume(true)
                }
            }
        }.getOrElse { continuation.resume(false) }
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
        parseStakingInfoResult(text)
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

private suspend fun createStakingDelegatorId() = suspendCoroutine { continuation ->
    runCatching {
        runBlocking {
            val provider = StakingManager.providers().firstOrNull { it.isLilico() } ?: return@runBlocking
            logd(TAG, "createStakingDelegatorId providerId：${provider.id}")
            val txid = CADENCE_CREATE_STAKE_DELEGATOR_ID.transactionByMainWallet {
                arg { string(provider.id) }
                arg { ufix64Safe(BigDecimal(0.000)) }
            }
            logd(TAG, "createStakingDelegatorId txid：$txid")
            TransactionStateWatcher(txid!!).watch { result ->
                if (result.isExecuteFinished()) {
                    continuation.resume(true)
                }
            }
        }
    }.getOrElse { continuation.resume(false) }
}

private suspend fun setupStaking(callback: () -> Unit) {
    logd(TAG, "setupStaking start")
    runCatching {
        val txid = CADENCE_SETUP_STAKING.transactionByMainWallet {} ?: return
        logd(TAG, "setupStaking txid:$txid")
        TransactionStateWatcher(txid).watch { result ->
            if (result.isExecuteFinished()) {
                logd(TAG, "setupStaking finish")
                callback.invoke()
            }
        }
    }.getOrElse { callback.invoke() }
}

data class StakingInfo(
    val nodeID: Float = 0.0f,
    val tokensCommitted: Float = 0.0f,
    val tokensStaked: Float = 0.0f,
    val tokensUnstaking: Float = 0.0f,
    val tokensRewarded: Float = 0.0f,
    val tokensUnstaked: Float = 0.0f,
    val tokensRequestedToUnstake: Float = 0.0f,
    val delegatorId: Int? = null,
)

data class StakingCache(
    val info: StakingInfo? = null,
    val apy: Float = DEFAULT_APY,
)