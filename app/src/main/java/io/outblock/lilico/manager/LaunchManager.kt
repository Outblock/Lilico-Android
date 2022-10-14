package io.outblock.lilico.manager

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import io.outblock.lilico.firebase.config.initFirebaseConfig
import io.outblock.lilico.firebase.firebaseInitialize
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.app.AppLifecycleObserver
import io.outblock.lilico.manager.app.PageLifecycleObserver
import io.outblock.lilico.manager.app.refreshChainNetwork
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.manager.config.NftCollectionConfig
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.nft.NftCollectionStateManager
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.manager.walletconnect.WalletConnect
import io.outblock.lilico.service.MessagingService
import io.outblock.lilico.utils.*
import io.outblock.lilico.wallet.restoreMnemonicV0

object LaunchManager {

    fun init(application: Application) {
        application.startServiceSafe(Intent(application, MessagingService::class.java))
        PageLifecycleObserver.init(application)
        AppLifecycleObserver.observe()
        safeRun { System.loadLibrary("TrustWalletCore") }
        asyncInit()
        readPreference()
        firebaseInitialize(application)
        initFirebaseConfig()
        setNightMode()
        runWorker(application)
        readCache()
        runCompatibleScript()
        WalletConnect.init(application)
    }

    private fun asyncInit() {
        ioScope {
            FlowApi.refreshConfig()
        }
    }

    private fun readPreference() {
        refreshChainNetwork()
    }

    private fun readCache() {
        NftCollectionConfig.sync()
        BalanceManager.reload()
        TransactionStateManager.reload()
        TokenStateManager.reload()
        NftCollectionStateManager.reload()
        CoinRateManager.init()
    }

    private fun setNightMode() {
        uiScope { AppCompatDelegate.setDefaultNightMode(getThemeMode()) }
    }

    private fun runWorker(application: Application) {
    }

    /**
     * Run compatible script if necessary
     * Sometimes the version upgrade and modification configuration have to be compatible with the old version
     */
    private fun runCompatibleScript() {
        restoreMnemonicV0()
    }
}