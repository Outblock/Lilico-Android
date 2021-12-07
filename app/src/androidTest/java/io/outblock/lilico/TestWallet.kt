package io.outblock.lilico

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import wallet.core.jni.HDWallet

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestWallet {
    init {
        System.loadLibrary("TrustWalletCore");
    }

    @Test
    fun testCreateMnemonic() {
        Log.w("TEST START", "testCreateMnemonic()")
        val wallet = HDWallet(128, "")
        Log.w("MNEMONIC", wallet.mnemonic())
    }
}