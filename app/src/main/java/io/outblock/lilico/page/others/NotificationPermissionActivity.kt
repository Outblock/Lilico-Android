package io.outblock.lilico.page.others

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.permissionx.guolindev.PermissionX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.page.main.MainActivity

class NotificationPermissionActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_permission)
        findViewById<View>(R.id.start_button).setOnClickListener { finish() }
        findViewById<View>(R.id.start_button).setOnClickListener {
            PermissionX.init(this).permissions(Manifest.permission.POST_NOTIFICATIONS).request { allGranted, _, _ ->
                MainActivity.launch(this)
            }
        }

        findViewById<View>(R.id.cancel_button).setOnClickListener { MainActivity.launch(this) }
    }

    override fun finish() {
        super.finish()
        MainActivity.launch(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, NotificationPermissionActivity::class.java))
        }
    }
}