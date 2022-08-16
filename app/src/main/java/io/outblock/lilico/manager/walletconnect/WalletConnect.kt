package io.outblock.lilico.manager.walletconnect

import android.app.Application
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loge

private val TAG = WalletConnect::class.java.simpleName

class WalletConnect {

    fun pair(uri: String) {
        SignClient.pair(Sign.Params.Pair(uri)) { error -> loge(error.throwable) }
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
        name = "Lilico",
        description = "A crypto wallet on Flow built for Explorers, Collectors and Gamers",
        url = "https://lilico.app",
        icons = listOf("https://lilico.app/logo.png"),
        redirect = null,
    )

    SignClient.initialize(Sign.Params.Init(
        application = application,
        relayServerUrl = "wss://relay.walletconnect.com?projectId=1dd2dfa085b9cf69ad5d316bfc11999f",
        metadata = appMetaData,
    ), onError = { error ->
        loge(error.throwable)
    })
    SignClient.setWalletDelegate(WalletConnectDelegate())
}