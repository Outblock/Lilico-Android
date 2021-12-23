package io.outblock.lilico.manager.biometric

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge
import java.util.concurrent.Executor


object BlockBiometricManager {
    private val TAG = BlockBiometricManager::class.java.simpleName

    fun checkIsBiometricEnable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticateCode = biometricManager.canAuthenticate(BIOMETRIC_WEAK)
        when (authenticateCode) {
            BiometricManager.BIOMETRIC_SUCCESS -> logd(TAG, "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> loge(TAG, "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> loge(TAG, "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> loge(TAG, "Biometric features are currently none enrolled.")
            else -> loge(TAG, "Biometric features authenticateCode:$authenticateCode")
        }
        return authenticateCode == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(activity: FragmentActivity) {
        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        val executor = Executor { Handler(Looper.getMainLooper()).post(it) }
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                loge(TAG, "Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val authenticatedCryptoObject = result.cryptoObject
                logd(TAG, "onAuthenticationSucceeded:${result.cryptoObject}")
                // User has verified the signature, cipher, or message
                // authentication code (MAC) associated with the crypto object,
                // so you can use it in your app's crypto-driven workflows.
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                loge(TAG, "Authentication failed")
            }
        })
        biometricPrompt.authenticate(promptInfo)
    }
}