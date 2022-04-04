package io.outblock.lilico.widgets.webview.fcl.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FclAuthnResponse(
    @SerializedName("body")
    val body: Body,
    @SerializedName("config")
    val config: Config,
    @SerializedName("fclVersion")
    val fclVersion: String,
    @SerializedName("service")
    val service: Service,
    @SerializedName("type")
    val type: String
) : Parcelable {
    @Parcelize
    data class Body(
        @SerializedName("extensions")
        val extensions: List<Extension>,
        @SerializedName("timestamp")
        val timestamp: Long
    ) : Parcelable {
        @Parcelize
        data class Extension(
            @SerializedName("endpoint")
            val endpoint: String,
            @SerializedName("f_type")
            val fType: String,
            @SerializedName("f_vsn")
            val fVsn: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("identity")
            val identity: Identity,
            @SerializedName("method")
            val method: String,
            @SerializedName("provider")
            val provider: Provider,
            @SerializedName("type")
            val type: String,
            @SerializedName("uid")
            val uid: String
        ) : Parcelable {
            @Parcelize
            data class Identity(
                @SerializedName("address")
                val address: String
            ) : Parcelable

            @Parcelize
            data class Provider(
                @SerializedName("address")
                val address: String,
                @SerializedName("description")
                val description: String,
                @SerializedName("icon")
                val icon: String,
                @SerializedName("name")
                val name: String
            ) : Parcelable
        }
    }

    @Parcelize
    data class Config(
        @SerializedName("client")
        val client: Client,
    ) : Parcelable {

        @Parcelize
        data class Client(
            @SerializedName("fclLibrary")
            val fclLibrary: String,
            @SerializedName("fclVersion")
            val fclVersion: String,
            @SerializedName("hostname")
            val hostname: String
        ) : Parcelable
    }

    @Parcelize
    data class Service(
        @SerializedName("type")
        val type: String
    ) : Parcelable
}