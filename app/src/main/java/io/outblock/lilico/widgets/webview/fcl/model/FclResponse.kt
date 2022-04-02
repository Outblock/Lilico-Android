package io.outblock.lilico.widgets.webview.fcl.model


import com.google.gson.annotations.SerializedName

data class FclResponse(
    @SerializedName("config")
    val config: Config,
    @SerializedName("fclVersion")
    val fclVersion: String,
    @SerializedName("service")
    val service: Service,
    @SerializedName("type")
    val type: String
) {

    fun serviceType() = service.type

    data class Config(
        @SerializedName("app")
        val app: App,
        @SerializedName("client")
        val client: Client,
        @SerializedName("discoveryAuthnInclude")
        val discoveryAuthnInclude: List<Any>,
        @SerializedName("services")
        val services: Services
    ) {
        class App

        data class Client(
            @SerializedName("fclLibrary")
            val fclLibrary: String,
            @SerializedName("fclVersion")
            val fclVersion: String,
            @SerializedName("hostname")
            val hostname: String
        )

        class Services
    }

    data class Service(
        @SerializedName("type")
        val type: String
    )
}