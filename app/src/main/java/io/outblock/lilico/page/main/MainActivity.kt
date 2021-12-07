package io.outblock.lilico.page.main

import android.os.Bundle
import android.util.Log
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import wallet.core.jni.HDWallet

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        System.loadLibrary("TrustWalletCore")
        val wallet = HDWallet(128, "")
        Log.w("MNEMONIC", wallet.mnemonic())
    }
}