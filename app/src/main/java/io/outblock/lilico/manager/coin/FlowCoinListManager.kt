package io.outblock.lilico.manager.coin

import android.os.Parcelable
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import kotlinx.parcelize.Parcelize
import java.util.concurrent.CopyOnWriteArrayList

object FlowCoinListManager {
    private val TAG = FlowCoinListManager::class.java.simpleName
    private const val KEY = "flow_coins"
    private val coinList = CopyOnWriteArrayList<FlowCoin>()

    fun reload() {
        ioScope {
            val jsonStr = Firebase.remoteConfig.getString(KEY)
            logd(TAG, "json:$jsonStr")
            val list = Gson().fromJson<List<FlowCoin>>(jsonStr, object : TypeToken<List<FlowCoin>>() {}.type)
            if (list.isNotEmpty()) {
                coinList.clear()
                coinList.addAll(list.filter { it.address().isNotBlank() })
            }
            TokenStateManager.fetchState()
        }
    }

    fun coinList() = coinList.toList()

    fun getCoin(symbol: String) = coinList.firstOrNull { it.symbol == symbol }

    fun getEnabledCoinList() = coinList.toList().filter { TokenStateManager.isTokenAdded(it.address()) }
}

@Parcelize
class FlowCoin(
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: FlowCoinAddress,
    @SerializedName("contract_name")
    val contractName: String,
    @SerializedName("storage_path")
    val storagePath: FlowCoinStoragePath,
    @SerializedName("decimal")
    val decimal: Int?,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("website")
    val website: String?,
) : Parcelable {
    fun address() = if (isTestnet()) address.testnet.orEmpty() else address.mainnet.orEmpty()

    fun isFlowCoin() = symbol.lowercase() == "flow"

    companion object {
        const val SYMBOL_FLOW = "flow"
        const val SYMBOL_FUSD = "fusd"
        const val SYMBOL_STFLOW = "stFlow"
        const val SYMBOL_BLT = "blt"
        const val SYMBOL_USDC = "usdc"
        const val SYMBOL_MY = "my"
        const val SYMBOL_THUL = "thul"
        const val SYMBOL_STARLY = "STARLY"
    }
}

@Parcelize
class FlowCoinAddress(
    @SerializedName("mainnet")
    val mainnet: String?,
    @SerializedName("testnet")
    val testnet: String?,
) : Parcelable

@Parcelize
class FlowCoinStoragePath(
    @SerializedName("balance")
    val balance: String,
    @SerializedName("vault")
    val vault: String,
    @SerializedName("receiver")
    val receiver: String,
) : Parcelable

fun FlowCoin.formatCadence(cadence: String): String {
    return cadence.replace("<Token>", contractName)
        .replace("<TokenAddress>", address())
        .replace("<TokenReceiverPath>", storagePath.receiver)
        .replace("<TokenBalancePath>", storagePath.balance)
        .replace("<TokenStoragePath>", storagePath.vault)
}

