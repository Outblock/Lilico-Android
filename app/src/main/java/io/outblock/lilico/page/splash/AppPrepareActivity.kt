package io.outblock.lilico.page.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.firebase.auth.firebaseJwt
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.toast

class AppPrepareActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_prepare)
        ioScope {
            firebaseJwt { isSuccessful, exception ->
                if (isSuccessful) {
                    MainActivity.launch(this)
                } else {
                    toast(msg = "init failed")
                }
                loge(exception)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AppPrepareActivity::class.java))
        }
    }
}