package io.outblock.lilico.page.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.outblock.lilico.manager.account.isAccountV1DataExist
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.others.AccountMigrateActivity
import io.outblock.lilico.utils.getUsername
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope

@SuppressLint("CustomSplashScreen")
open class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        logd("startup", "SplashActivity onCreate")
        uiScope {
            if (isAccountV1DataExist()) {
                AccountMigrateActivity.launch(this)
            } else {
                MainActivity.launch(this)
            }
            logd("xxx", "username:${getUsername()}")
            overridePendingTransition(0, 0)
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
        overridePendingTransition(0, 0)
    }
}