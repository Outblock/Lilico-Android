package io.outblock.lilico.manager.config

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.readTextFromAssets

object NftCollectionConfig {

    private val config = mutableListOf<NftCollection>()

    fun sync() {
        ioScope {
            val text = readTextFromAssets("config/collections.json")
            config.clear()
            config.addAll(Gson().fromJson(text, object : TypeToken<List<NftCollection>>() {}.type))
        }
    }

    fun get(address: String): NftCollection? {
        return config.firstOrNull { it.address == address }
    }
}

class NftCollection(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("logo")
    val logo: String,

    @SerializedName("banner")
    val banner: String,

    @SerializedName("official_website")
    val officialWebsite: String,

    @SerializedName("marketplace")
    val marketplace: String,
)