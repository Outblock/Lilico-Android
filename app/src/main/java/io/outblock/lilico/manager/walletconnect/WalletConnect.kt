package io.outblock.lilico.manager.walletconnect

import android.app.Application
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.logw

private val TAG = WalletConnect::class.java.simpleName

class WalletConnect {

    fun pair(uri: String) {
        SignClient.pair(Sign.Params.Pair(uri)) { error -> loge(error.throwable) }
    }

    fun sessionCount(): Int = sessions().size

    fun sessions() = SignClient.getListOfSettledSessions().filter { it.metaData != null }

    fun disconnect(topic: String) {
        SignClient.disconnect(
            Sign.Params.Disconnect(sessionTopic = topic)
        ) { error -> loge(error.throwable) }
    }

    companion object {
        private lateinit var instance: WalletConnect

        fun init(application: Application) {
            ioScope {
                setup(application)
                instance = WalletConnect()
            }
        }

        fun get() = instance
    }
}

private fun setup(application: Application) {
    val appMetaData = Sign.Model.AppMetaData(
        name = "Lilico Android",
        description = "A crypto wallet on Flow built for Explorers, Collectors and Gamers",
        url = "https://lilico.app",
        icons = listOf("https://lilico.app/logo.png"),
        redirect = null,
    )

    SignClient.initialize(Sign.Params.Init(
        application = application,
        relayServerUrl = "wss://relay.walletconnect.com?projectId=29b38ec12be4bd19bf03d7ccef29aaa6",
        metadata = appMetaData,
        connectionType = Sign.ConnectionType.MANUAL,
    ), onError = { error ->
        loge(error.throwable)
    })
    SignClient.setWalletDelegate(WalletConnectDelegate())
    SignClient.WebSocket.open { error -> logw(TAG, "open error:$error") }
}