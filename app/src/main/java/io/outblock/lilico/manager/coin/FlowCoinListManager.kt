package io.outblock.lilico.manager.coin

import android.os.Parcelable
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isDev
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
                coinList.addAll(list)
            }
        }
    }

    fun coinList() = coinList
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
    val decimal: Int,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("website")
    val website: String,
) : Parcelable {
    fun address() = if (isDev()) address.testnet else address.mainnet
}

@Parcelize
class FlowCoinAddress(
    val mainnet: String,
    val testnet: String,
) : Parcelable

@Parcelize
class FlowCoinStoragePath(
    val balance: String,
    val vault: String,
    val receiver: String,
) : Parcelable

fun FlowCoin.formatCadence(cadence: String): String {
    var script = cadence
    script = script.replace("<Token>", contractName)
    script = script.replace("<TokenAddress>", address())
    script = script.replace("<TokenReceiverPath>", storagePath.receiver)
    script = script.replace("<TokenBalancePath>", storagePath.balance)
    script = script.replace("<TokenStoragePath>", storagePath.vault)
    return script
}

