package io.outblock.lilico

import android.app.Application
import io.outblock.lilico.utils.Env

class LilicoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Env.init(this)
    }
}