package io.outblock.lilico

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import wallet.core.jni.HDWallet

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        println("TEST START")
        System.loadLibrary("TrustWalletCore")
        val wallet = HDWallet(128, "")
        println("MNEMONIC:${wallet.mnemonic()}")
    }
}