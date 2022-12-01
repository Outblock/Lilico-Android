package io.outblock.lilico.manager.staking

import androidx.annotation.WorkerThread
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.stakingCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.manager.flowjvm.*
import io.outblock.lilico.manager.transaction.TransactionStateWatcher
import io.outblock.lilico.manager.transaction.isExecuteFinished
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.logv
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val DEFAULT_APY = 0.093f
const val STAKING_DEFAULT_NORMAL_APY = 0.08f
private const val TAG = "StakingManager"

object StakingManager {

    private var stakingInfo = StakingInfo()
    private var apy = DEFAULT_APY
    private var apyYear = DEFAULT_APY

    private val providers = StakingProviders().apply { refresh() }

    private val delegatorIds = ConcurrentHashMap<String, Int>()

    fun init() {
        ioScope {
            val cache = stakingCache().read()
            stakingInfo = cache?.info ?: StakingInfo()
            apy = cache?.apy ?: apy
            apyYear = cache?.apyYear ?: apyYear
        }
    }

    fun stakingInfo() = stakingInfo

    fun providers() = providers.get()

    fun delegatorIds() = delegatorIds.toMap()

    fun apy() = apy

    fun apyYear() = apyYear

    fun isStaked(): Boolean {
        if (stakingInfo.nodes.isEmpty()) {
            refresh()
        }
        return stakingCount() > 0.0f
    }

    fun stakingCount() = stakingInfo.nodes.sumOf { it.tokensCommitted.toDouble() + it.tokensStaked }.toFloat()

    fun refresh() {
        ioScope {
            updateApy()
            stakingInfo = queryStakingInfo() ?: stakingInfo
            if (stakingInfo.nodes.isEmpty()) {
                prepare()
            }
            stakingInfo = queryStakingInfo() ?: stakingInfo
            refreshDelegatorInfo()
            cache()
        }
    }

    suspend fun refreshDelegatorInfo() {
        val ids = getDelegatorInfo()
        if (ids.isNotEmpty()) {
            delegatorIds.clear()
            delegatorIds.putAll(ids)
            logd(TAG, "delegatorIds:$delegatorIds")
        }
    }

    private fun updateApy() {
        queryStakingApy(CADENCE_GET_STAKE_APY_BY_WEEK)?.let {
            apy = it
            cache()
        }

        queryStakingApy(CADENCE_GET_STAKE_APY_BY_YEAR)?.let {
            apyYear = it
            cache()
        }
    }

    private suspend fun prepare() = suspendCoroutine { continuation ->
        runCatching {
            runBlocking {
                setupStaking {
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
        logv(TAG, "queryStakingInfo response:$text")
        parseStakingInfoResult(text)
    }.getOrNull()
}

private fun queryStakingApy(cadence: String): Float? {
    return runCatching {
        val response = cadence.executeCadence {}
        val apy = response?.parseFloat()
        logd(TAG, "queryStakingApy apy:$apy")
        if (apy == 0.0f) null else apy
    }.getOrNull()
}

suspend fun createStakingDelegatorId(provider: StakingProvider) = suspendCoroutine { continuation ->
    runCatching {
        runBlocking {
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
        if (hasBeenSetup()) {
            return
        }
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

private suspend fun getDelegatorInfo() = suspendCoroutine { continuation ->
    logd(TAG, "getDelegatorInfo start")
    runCatching {
        val address = walletCache().read()?.primaryWalletAddress()!!
        val response = CADENCE_GET_DELEGATOR_INFO.executeCadence {
            arg { address(address) }
        }!!
        logv(TAG, "getDelegatorInfo response:${String(response.bytes)}")
        continuation.resume(parseStakingDelegatorInfo(String(response.bytes)))
    }.getOrElse { continuation.resume(mapOf<String, Int>()) }
}

@WorkerThread
private fun hasBeenSetup(): Boolean {
    return runCatching {
        val address = walletCache().read()?.primaryWalletAddress()!!
        val response = CADENCE_CHECK_IS_STAKING_SETUP.executeCadence { arg { address(address) } }
        response?.parseBool(false) ?: false
    }.getOrElse { false }
}

data class StakingInfo(
    @SerializedName("nodes")
    val nodes: List<StakingNode> = emptyList(),
)

data class StakingNode(
    @SerializedName("delegatorId")
    val delegatorId: Int? = null,
    @SerializedName("nodeID")
    val nodeID: String = "",
    @SerializedName("tokensCommitted")
    val tokensCommitted: Float = 0.0f,
    @SerializedName("tokensStaked")
    val tokensStaked: Float = 0.0f,
    @SerializedName("tokensUnstaking")
    val tokensUnstaking: Float = 0.0f,
    @SerializedName("tokensRewarded")
    val tokensRewarded: Float = 0.0f,
    @SerializedName("tokensUnstaked")
    val tokensUnstaked: Float = 0.0f,
    @SerializedName("tokensRequestedToUnstake")
    val tokensRequestedToUnstake: Float = 0.0f,
)

data class StakingCache(
    @SerializedName("info")
    val info: StakingInfo? = null,
    @SerializedName("apy")
    val apy: Float = DEFAULT_APY,
    @SerializedName("apyYear")
    val apyYear: Float = DEFAULT_APY,
)

fun StakingNode.stakingCount() = tokensCommitted + tokensStaked