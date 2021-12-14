package io.outblock.lilico.page.walletcreate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.page.walletcreate.fragments.mnemonic.CreateMnemonicFragment

class WalletCreateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateMnemonicFragment()).commit()

        title = "Create Wallet"
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, WalletCreateActivity::class.java))
        }
    }
}