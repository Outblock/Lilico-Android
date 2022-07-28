package io.outblock.lilico.page.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.logd

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        logd("startup", "SplashActivity onCreate")
        MainActivity.launch(this)
        overridePendingTransition(0, 0)
    }

    override fun onStop() {
        super.onStop()
        finish()
        overridePendingTransition(0, 0)
    }
}