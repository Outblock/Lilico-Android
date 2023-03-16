package io.outblock.lilico.manager.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isFreeGasPreferenceEnable
import io.outblock.lilico.utils.safeRun

suspend fun isGasFree() = AppConfig.isFreeGas() && isFreeGasPreferenceEnable()

object AppConfig {

    private var config: Config? = null

    fun isFreeGas() = config().features.freeGas

    fun payer() = if (isTestnet()) config().payer.testnet else config().payer.mainnet

    fun walletConnectEnable() = config().features.walletConnect

    fun sync() {
        ioScope { reloadConfig() }
    }

    private fun reloadConfig(): Config {
        val text = Firebase.remoteConfig.getString("free_gas_config")
        safeRun {
            config = Gson().fromJson(text, Config::class.java)
        }
        return config!!
    }

    private fun config() = config ?: reloadConfig()
}

private data class Config(
    @SerializedName("features")
    val features: Features,
    @SerializedName("payer")
    val payer: Payer
) {
}

private data class Features(
    @SerializedName("free_gas")
    val freeGas: Boolean,
    @SerializedName("wallet_connect")
    val walletConnect: Boolean,
)

private data class Payer(
    @SerializedName("mainnet")
    val mainnet: PayerNet,
    @SerializedName("testnet")
    val testnet: PayerNet
)

data class PayerNet(
    @SerializedName("address")
    val address: String,
    @SerializedName("keyId")
    val keyId: Int
)