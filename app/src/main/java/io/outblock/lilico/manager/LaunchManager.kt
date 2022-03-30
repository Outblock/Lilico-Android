package io.outblock.lilico.manager

import android.app.Application
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.outblock.lilico.firebase.config.initFirebaseConfig
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.app.PageLifecycleObserver
import io.outblock.lilico.manager.app.refreshChainNetwork
import io.outblock.lilico.manager.coin.CoinRateManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.manager.worker.JWTReloadWorker
import io.outblock.lilico.service.MessagingService
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.startServiceSafe
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
        CoinRateManager.init()
    }

    private fun setNightMode() {
//        when (getNightModeSetting()) {
//            NIGHT_MODE_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            NIGHT_MODE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//        }

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun runWorker(application: Application) {
        WorkManager.getInstance(application).enqueue(PeriodicWorkRequestBuilder<JWTReloadWorker>(2, TimeUnit.MINUTES).build())
    }
}