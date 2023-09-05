package io.outblock.lilico.page.others

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.manager.account.accountMigrateV1
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.isNotificationPermissionGrand

class AccountMigrateActivity : NewFeatureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountMigrateV1()
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AccountMigrateActivity::class.java))
        }
    }
}

open class NewFeatureActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_feature)
        findViewById<View>(R.id.start_button).setOnClickListener { finish() }
    }

    override fun finish() {
        super.finish()
        MainActivity.launch(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }
}