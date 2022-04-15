package io.outblock.lilico.page.splash

import android.os.Bundle
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.logd

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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