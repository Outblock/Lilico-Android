package io.outblock.lilico

import android.app.Application
import io.outblock.lilico.manager.LaunchManager
import io.outblock.lilico.utils.Env

class LilicoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Env.init(this)
        LaunchManager.init(this)
    }
}