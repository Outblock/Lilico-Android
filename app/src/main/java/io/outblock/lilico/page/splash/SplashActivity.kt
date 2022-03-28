package io.outblock.lilico.page.splash

import android.os.Bundle
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.firebase.auth.isJwtTokenExpire
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("startup", "SplashActivity onCreate")
        uiScope {
            if (isJwtTokenExpire()) {
                AppPrepareActivity.launch(this)
            } else {
                MainActivity.launch(this)
            }
            overridePendingTransition(0, 0)
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
        overridePendingTransition(0, 0)
    }

}