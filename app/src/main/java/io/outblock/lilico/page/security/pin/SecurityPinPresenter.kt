package io.outblock.lilico.page.security.pin

import android.content.Intent
import io.outblock.lilico.R
import io.outblock.lilico.databinding.ActivitySecurityPinBinding
import io.outblock.lilico.page.security.pin.SecurityPinActivity.Companion.TYPE_CHECK
import io.outblock.lilico.page.security.pin.SecurityPinActivity.Companion.TYPE_RESET
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.KeyboardItem
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.KeyboardType
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.keyboardT9Normal
import io.outblock.lilico.wallet.getMnemonicAesKey
import io.outblock.lilico.wallet.updateMnemonicAesKey

class SecurityPinPresenter(
    private val activity: SecurityPinActivity,
    private val binding: ActivitySecurityPinBinding,
    private val type: Int,
    private val action: Intent?,
) {

    private var verifyPassed = false

    init {
        with(binding) {
            pinInput.setCheckCallback { passed ->
                if (passed && type == TYPE_RESET) {
                    updateMnemonicAesKey(pinInput.keys().joinToString("") { "${it.number}" })
                    checkFinish()
                }
            }
            pinKeyboard.setOnKeyboardActionListener { onKeyPressed(it) }
            pinKeyboard.bindKeys(keyboardT9Normal, KeyboardType.T9)
            pinKeyboard.show()
            if (type == TYPE_CHECK) {
                hintTextView.setText(R.string.verify_pin_code)
            } else if (type == TYPE_RESET) {
                hintTextView.setText(R.string.enter_current_pin_code)
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
            if (type == TYPE_CHECK) {
                if (getMnemonicAesKey() == pinCode) {
                    checkFinish()
                }
            } else if (type == TYPE_RESET) {
                if (!verifyPassed) {
                    if (getMnemonicAesKey() != pinCode) {
                        shakeAndClear(keysCLear = true)
                    } else {
                        verifyPassed = true
                        clear(keysCLear = true)
                        binding.hintTextView.setText(R.string.create_pin)
                    }
                } else if (!isChecking()) {
                    binding.hintTextView.setText(R.string.check_pin)
                    checking()
                }
            }
        }
    }

    private fun checkFinish() {
        action?.let { activity.startActivity(it) }
        activity.finish()
    }
}