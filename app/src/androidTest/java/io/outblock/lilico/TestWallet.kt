package io.outblock.lilico

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nftco.flow.sdk.bytesToHex
import org.junit.Test
import org.junit.runner.RunWith
import wallet.core.jni.CoinType
import wallet.core.jni.Curve
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
        Log.w("method", "testCreateMnemonic()")
        val wallet = HDWallet(128, "")
        Log.w("mnemonic", wallet.mnemonic())
    }

    @Test
    fun testCreateWalletFromMnemonic() {
        Log.w("method", "testCreateMnemonic()")
        val wallet = HDWallet(MNEMONIC, "")
        Log.w("Flow address", wallet.getAddressForCoin(CoinType.FLOW))
    }

    @Test
    fun testWalletKey() {
        Log.w("method", "testWalletKey()")
        val wallet = HDWallet(MNEMONIC, "")
        Log.w("Flow public key", wallet.getKeyForCoin(CoinType.FLOW).publicKeyNist256p1.data().bytesToHex())
    }

    @Test
    fun testWalletSign() {
        Log.w("method", "testWalletSign()")
        val wallet = HDWallet(MNEMONIC, "")
        val sign = wallet.getKeyForCoin(CoinType.FLOW).sign(byteArrayOf(0x00), Curve.SECP256K1)
        Log.w("sign raw", "$sign")
        Log.w("sign hex", sign?.bytesToHex().orEmpty())
    }


    companion object {
        const val MNEMONIC = "pride mutual banner proud coil thunder question soccer stone uniform museum wild"
        const val PUBLIC_KEY = "0x02af7dbda5c7a708bac1f4da132b23c757aa532fd1afb57aa15145f9289d49b043"

        // TODO 乱写的
        const val PRIVATE_KEY = "a2f983853e61b3e27d94b7bf3d7094dd756aead2a813dd5cf738e1da56fa9c17"

        const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
        const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
        const val HOST_CANARYNET = "access.canary.nodes.onflow.org"
    }
}