package io.outblock.lilico.manager.config

import android.os.Parcelable
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import kotlinx.parcelize.Parcelize

object NftCollectionConfig {
    private const val KEY = "nft_collections"

    private val config = mutableListOf<NftCollection>()

    fun sync() {
        ioScope {
            val text = Firebase.remoteConfig.getString(KEY)
            logd("NftCollectionConfig", text.take(300))
            config.clear()
            config.addAll(Gson().fromJson(text, object : TypeToken<List<NftCollection>>() {}.type))
        }
    }

    fun get(address: String): NftCollection? {
        return config.firstOrNull { it.address() == address }
    }
}

class NftCollection(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: NftCollectionAddress,

    @SerializedName("logo")
    val logo: String,

    @SerializedName("banner")
    val banner: String,

    @SerializedName("official_website")
    val officialWebsite: String,

    @SerializedName("marketplace")
    val marketplace: String,
) {
    //    fun address() = if (isDev()) address.testnet else address.mainnet
    fun address() = address.mainnet
}

@Parcelize
class NftCollectionAddress(
    @SerializedName("mainnet")
    val mainnet: String,
    @SerializedName("testnet")
    val testnet: String,
) : Parcelable