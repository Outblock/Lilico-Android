package io.outblock.lilico.page.security

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import io.outblock.lilico.manager.biometric.BlockBiometricManager
import io.outblock.lilico.page.security.pin.SecurityPinActivity
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isBiometricEnable
import io.outblock.lilico.utils.uiScope


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
            uiScope { SecurityPinActivity.launch(this, SecurityPinActivity.TYPE_CHECK, action = action) }
        }
    }
}