package io.outblock.lilico.page.security.pin

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.outblock.lilico.R
import io.outblock.lilico.databinding.ActivitySecurityPinBinding
import io.outblock.lilico.page.security.pin.SecurityPinActivity.Companion.TYPE_CHECK
import io.outblock.lilico.page.security.pin.SecurityPinActivity.Companion.TYPE_CREATE
import io.outblock.lilico.page.security.pin.SecurityPinActivity.Companion.TYPE_RESET
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.KeyboardItem
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.KeyboardType
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.keyboardT9Normal
import io.outblock.lilico.utils.getPinCode
import io.outblock.lilico.utils.savePinCode

class SecurityPinPresenter(
    private val activity: SecurityPinActivity,
    private val binding: ActivitySecurityPinBinding,
    private val type: Int,
    private val action: Intent?,
    private val broadcastAction: String? = null,
) {

    private var verifyPassed = false

    init {
        with(binding) {
            pinInput.setCheckCallback { passed ->
                if (passed && (type == TYPE_RESET || type == TYPE_CREATE)) {
                    savePinCode(pinInput.keys().joinToString("") { "${it.number}" })
                    checkFinish()
                }
            }
            pinKeyboard.setOnKeyboardActionListener { onKeyPressed(it) }
            pinKeyboard.bindKeys(keyboardT9Normal, KeyboardType.T9)
            pinKeyboard.show()
            when (type) {
                TYPE_CHECK -> hintTextView.setText(R.string.verify_pin_code)
                TYPE_RESET -> hintTextView.setText(R.string.enter_current_pin_code)
                TYPE_CREATE -> hintTextView.setText(R.string.create_pin)
            }
        }
    }

    private fun ActivitySecurityPinBinding.onKeyPressed(key: KeyboardItem) {
        if (key.type == KeyboardItem.TYPE_DELETE_KEY || key.type == KeyboardItem.TYPE_CLEAR_KEY) {
            pinInput.delete()
        } else {
            pinInput.input(key)
            updateState()
        }
    }

    private fun updateState() {
        with(binding.pinInput) {
            if (keys().size != 6) {
                return
            }
            val pinCode = keys().joinToString("") { "${it.number}" }

            verifyCheckType(pinCode)
            verifyResetType(pinCode)
            verifyCreateType(pinCode)
        }
    }

    private fun verifyCheckType(pinCode: String) {
        if (type != TYPE_CHECK) return
        if (getPinCode() == pinCode) {
            checkFinish()
        } else {
            binding.pinInput.shakeAndClear(keysCLear = true)
        }
    }

    private fun verifyResetType(pinCode: String) {
        if (type != TYPE_RESET) return
        with(binding.pinInput) {
            if (!verifyPassed) {
                if (getPinCode() == pinCode) {
                    verifyPassed = true
                    clear(keysCLear = true)
                    binding.hintTextView.setText(R.string.check_pin)
                } else {
                    shakeAndClear(keysCLear = true)
                }
            } else if (!isChecking()) {
                binding.hintTextView.setText(R.string.check_pin)
                checking()
            }
        }
    }

    private fun verifyCreateType(pinCode: String) {
        if (type != TYPE_CREATE) return
        with(binding.pinInput) {
            if (!isChecking()) {
                binding.hintTextView.setText(R.string.check_pin)
                checking()
            }
        }
    }

    private fun checkFinish() {
        action?.let { activity.startActivity(it) }
        LocalBroadcastManager.getInstance(activity).sendBroadcast(Intent(broadcastAction).apply { putExtra("verify", true) })
        activity.finish()
    }
}