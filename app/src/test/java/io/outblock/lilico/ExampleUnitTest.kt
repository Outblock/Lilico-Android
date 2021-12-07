package io.outblock.lilico

import org.junit.Assert.assertEquals
import org.junit.Test
import wallet.core.jni.HDWallet

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        println("TEST START")
        System.loadLibrary("TrustWalletCore")
        val wallet = HDWallet(128, "")
        println("MNEMONIC:${wallet.mnemonic()}")
    }
}