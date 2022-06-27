package io.outblock.lilico.page.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.outblock.lilico.manager.biometric.BlockBiometricManager
import io.outblock.lilico.page.security.pin.SecurityPinActivity
import io.outblock.lilico.utils.getPinCode
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isBiometricEnable
import io.outblock.lilico.utils.uiScope
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


// check security then open page
fun FragmentActivity.securityOpen(action: Intent) {
    ioScope {
        if (isBiometricEnable()) {
            uiScope {
                BlockBiometricManager.showBiometricPrompt(this) { isSuccess ->
                    if (isSuccess) {
                        startActivity(action)
                    }
                }
            }
        } else {
            uiScope {
                if (getPinCode().isBlank()) {
                    startActivity(action)
                } else {
                    SecurityPinActivity.launch(this, SecurityPinActivity.TYPE_CHECK, action = action)
                }
            }
        }
    }
}

// check security
suspend fun securityVerification(activity: FragmentActivity) = suspendCoroutine<Boolean> { cont ->
    uiScope {
        if (isBiometricEnable()) {
            BlockBiometricManager.showBiometricPrompt(activity) { isSuccess ->
                cont.resume(isSuccess)
            }
        } else {
            cont.resume(securityPinCodeVerification(activity))
        }
    }
}

private suspend fun securityPinCodeVerification(activity: FragmentActivity) = suspendCoroutine<Boolean> { cont ->
    uiScope {
        if (getPinCode().isBlank()) {
            cont.resume(true)
        } else {
            val filter = "${System.currentTimeMillis()}"
            LocalBroadcastManager.getInstance(activity).registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    cont.resume(intent?.getBooleanExtra("verify", false) ?: false)
                    LocalBroadcastManager.getInstance(activity).unregisterReceiver(this)
                }
            }, IntentFilter(filter))
            SecurityPinActivity.launch(activity, SecurityPinActivity.TYPE_CHECK, broadcastAction = filter)
        }
    }
}