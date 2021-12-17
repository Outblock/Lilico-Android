package io.outblock.lilico.manager

import android.app.Application
import android.content.Intent
import io.outblock.lilico.service.MessagingService
import io.outblock.lilico.utils.ioScope

object LaunchManager {

    fun init(application: Application) {
        application.startService(Intent(application, MessagingService::class.java))
        asyncInit()
        setNightMode()
    }

    private fun asyncInit() {
        ioScope {
            System.loadLibrary("TrustWalletCore")
        }
    }

    private fun setNightMode() {
//        when (getNightModeSetting()) {
//            NIGHT_MODE_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            NIGHT_MODE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//        }

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}