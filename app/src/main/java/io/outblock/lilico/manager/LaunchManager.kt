package io.outblock.lilico.manager

import android.app.Application
import android.content.Intent
import io.outblock.lilico.service.MessagingService
import io.outblock.lilico.utils.ioScope

object LaunchManager {

    fun init(application: Application) {
        application.startService(Intent(application, MessagingService::class.java))
        asyncInit()
    }

    private fun asyncInit() {
        ioScope {
            System.loadLibrary("TrustWalletCore")
        }
    }
}