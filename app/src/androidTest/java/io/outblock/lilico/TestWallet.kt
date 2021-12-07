package io.outblock.lilico

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nftco.flow.sdk.bytesToHex
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import wallet.core.jni.*
import io.outblock.wallet.extensions.toHex
import io.outblock.wallet.extensions.toHexBytes

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

        assertEquals(Mnemonic.isValid(wallet.mnemonic()), true)
    }

    @Test
    fun testCreateWalletFromMnemonic() {
        Log.w("method", "testCreateMnemonic()")
        val wallet = HDWallet(MNEMONIC, "")
        assertEquals(MNEMONIC, wallet.mnemonic())
    }

    @Test
    fun testWalletKey() {
        Log.w("method", "testWalletKey()")
        val wallet = HDWallet(MNEMONIC, "")
        val privateKey = wallet.getDerivedKey(CoinType.FLOW, 0, 0, 0)
        assertEquals(true, PrivateKey.isValid(privateKey.data(), Curve.NIST256P1))
        assertEquals(PRIVATE_KEY, privateKey.data().bytesToHex())
        assertEquals(PUBLIC_KEY, privateKey.publicKeyNist256p1.uncompressed().data().bytesToHex())
    }

    @Test
    fun testWalletSign() {
        Log.w("method", "testWalletSign()")
        val data = "hello schnorr".encodeToByteArray()
        val hashedData = Hash.sha256(data)
        val wallet = HDWallet(MNEMONIC, "")
        val privateKey = wallet.getDerivedKey(CoinType.FLOW, 0, 0, 0)
        assertEquals(PRIVATE_KEY, privateKey.data().bytesToHex())
        val signature = privateKey.sign(hashedData, Curve.NIST256P1)
        Log.w("signature -> ", signature.toHex())
        assertEquals( true, privateKey.publicKeyNist256p1.verify(signature, hashedData))
        assertEquals( true, privateKey.publicKeyNist256p1.verify(SIGNATURE.toHexBytes(), hashedData))
    }


    companion object {
        const val MNEMONIC = "normal dune pole key case cradle unfold require tornado mercy hospital buyer"
        const val PUBLIC_KEY = "04dbe5b4b4416ad9158339dd692002ceddab895e11bd87d90ce7e3e745efef28d2ad6e736fe3d57d52213f397a7ba9f0bc8c65620a872aefedbc1ddd74c605cf58"
        const val PRIVATE_KEY = "638dc9ad0eee91d09249f0fd7c5323a11600e20d5b9105b66b782a96236e74cf"
        const val SIGNATURE = "0cd37adf53dc353eeb07321c765d81aedd11f34a6393de31bb15e2c5a07793c96ac54369d71a7e769dced55fc941d2f723538e1b31bf587e7f435e911222068b01"
        const val TEST_ADDRESS = "0xcfe03e16d57ad8ad"

        const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
        const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
        const val HOST_CANARYNET = "access.canary.nodes.onflow.org"
    }
}