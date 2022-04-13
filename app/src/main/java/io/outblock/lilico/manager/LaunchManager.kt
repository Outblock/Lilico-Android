package io.outblock.lilico.manager

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.outblock.lilico.firebase.config.initFirebaseConfig
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.app.PageLifecycleObserver
import io.outblock.lilico.manager.app.refreshChainNetwork
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.manager.nft.NftCollectionStateManager
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.manager.worker.JWTReloadWorker
import io.outblock.lilico.service.MessagingService
import io.outblock.lilico.utils.getThemeMode
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.startServiceSafe
import io.outblock.lilico.utils.uiScope
import java.util.concurrent.TimeUnit

object LaunchManager {

    fun init(application: Application) {
        application.startServiceSafe(Intent(application, MessagingService::class.java))
        PageLifecycleObserver.init(application)
        asyncInit()
        readPreference()
        initFirebaseConfig()
        setNightMode()
        runWorker(application)
        readCache()
    }

    private fun asyncInit() {
        ioScope {
            System.loadLibrary("TrustWalletCore")
        }
    }

    private fun readPreference() {
        refreshChainNetwork()
    }

    private fun readCache() {
        BalanceManager.init()
        TransactionStateManager.init()
        TokenStateManager.init()
        NftCollectionStateManager.init()
        CoinRateManager.init()
    }

    private fun setNightMode() {
        uiScope { AppCompatDelegate.setDefaultNightMode(getThemeMode()) }
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun runWorker(application: Application) {
        WorkManager.getInstance(application).enqueue(PeriodicWorkRequestBuilder<JWTReloadWorker>(2, TimeUnit.MINUTES).build())
    }
}