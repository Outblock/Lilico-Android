package io.outblock.lilico.manager.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.utils.NETWORK_SANDBOX
import io.outblock.lilico.utils.NETWORK_TESTNET
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isFreeGasPreferenceEnable
import io.outblock.lilico.utils.safeRun

suspend fun isGasFree() = AppConfig.isFreeGas() && isFreeGasPreferenceEnable()

object AppConfig {

    private var config: Config? = null
    private var flowAddressRegistry: FlowAddressRegistry? = null

    fun isFreeGas() = config().features.freeGas

    fun payer() = if (isTestnet()) config().payer.testnet else config().payer.mainnet

    fun walletConnectEnable() = config().features.walletConnect

    fun addressRegistry(network: Int): Map<String, String> {
        return when (network) {
            NETWORK_TESTNET -> flowAddressRegistry().testnet
            NETWORK_SANDBOX -> flowAddressRegistry().sandboxnet
            else -> flowAddressRegistry().mainnet
        }
    }

    fun sync() {
        ioScope {
            reloadConfig()
            reloadFlowAddressRegistry()
        }
    }

    private fun reloadConfig(): Config {
        val text = Firebase.remoteConfig.getString("free_gas_config")
        safeRun {
            config = Gson().fromJson(text, Config::class.java)
        }
        return config!!
    }

    private fun reloadFlowAddressRegistry(): FlowAddressRegistry {
        val text = Firebase.remoteConfig.getString("contract_address")
        safeRun {
            flowAddressRegistry = Gson().fromJson(text, FlowAddressRegistry::class.java)
        }
        return flowAddressRegistry!!
    }

    private fun config() = config ?: reloadConfig()

    private fun flowAddressRegistry() = flowAddressRegistry ?: reloadFlowAddressRegistry()
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

private data class FlowAddressRegistry(
    @SerializedName("mainnet")
    val mainnet: Map<String, String>,
    @SerializedName("testnet")
    val testnet: Map<String, String>,
    @SerializedName("sandboxnet")
    val sandboxnet: Map<String, String>,
)