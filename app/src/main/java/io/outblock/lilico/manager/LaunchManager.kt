package io.outblock.lilico.manager

import android.app.Application
import android.content.Intent
import io.outblock.lilico.service.MessagingService

object LaunchManager {

    fun init(application: Application) {
        application.startService(Intent(application, MessagingService::class.java))
    }
}