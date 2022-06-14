package io.outblock.lilico.manager.config

import android.os.Parcelable
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.manager.nft.NftCollectionStateManager
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.readTextFromAssets
import io.outblock.lilico.utils.safeRun
import kotlinx.parcelize.Parcelize

object NftCollectionConfig {
    private const val KEY = "nft_collections"

    private val config = mutableListOf<NftCollection>()

    fun sync() {
        ioScope {
            var text = Firebase.remoteConfig.getString(KEY)
            if (text.isBlank()) {
                text = readTextFromAssets("config/collections.json") ?: return@ioScope
            }
            logd("NftCollectionConfig", text.take(300))
            config.clear()
            safeRun { config.addAll(Gson().fromJson(text, object : TypeToken<List<NftCollection>>() {}.type)) }
            NftCollectionStateManager.fetchState()
        }
    }

    fun get(address: String): NftCollection? {
        return config.firstOrNull { it.address() == address }
    }

    fun list() = config.toList()
}

@Parcelize
data class NftCollection(
    @SerializedName("address")
    val address: Address,
    @SerializedName("banner")
    val banner: String,
    @SerializedName("contract_name")
    val contractName: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("marketplace")
    val marketplace: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("official_website")
    val officialWebsite: String,
    @SerializedName("path")
    val path: Path
) : Parcelable {

    fun address(forceMainnet: Boolean = false) =
        if (forceMainnet) address.mainnet else (if (isTestnet()) address.testnet.orEmpty() else address.mainnet)

    @Parcelize
    data class Address(
        @SerializedName("mainnet")
        val mainnet: String,
        @SerializedName("testnet")
        val testnet: String? = null,
    ) : Parcelable

    @Parcelize
    data class Path(
        @SerializedName("public_collection_name")
        val publicCollectionName: String,
        @SerializedName("public_path")
        val publicPath: String,
        @SerializedName("storage_path")
        val storagePath: String
    ) : Parcelable
}