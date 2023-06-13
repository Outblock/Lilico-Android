package io.outblock.lilico.page.others

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.manager.account.accountMigrateV1
import io.outblock.lilico.page.main.MainActivity

class AccountMigrateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_migrate)
        accountMigrateV1()
    }

    override fun onDestroy() {
        MainActivity.launch(this)
        super.onDestroy()
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AccountMigrateActivity::class.java))
        }
    }
}