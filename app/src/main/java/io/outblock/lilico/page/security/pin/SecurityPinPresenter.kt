package io.outblock.lilico.page.security.pin

import io.outblock.lilico.databinding.ActivitySecurityPinBinding
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.KeyboardItem
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.KeyboardType
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.keyboardT9Normal

class SecurityPinPresenter(
    private val activity: SecurityPinActivity,
    private val binding: ActivitySecurityPinBinding,
) {

    init {
        with(binding) {
            pinInput.setCheckCallback { passed ->
//                updateMnemonicAesKey(pinInput.keys().joinToString("") { "${it.number}" })
            }
            pinKeyboard.setOnKeyboardActionListener { onKeyPressed(it) }
            pinKeyboard.bindKeys(keyboardT9Normal, KeyboardType.T9)
            pinKeyboard.show()
        }
    }

    private fun ActivitySecurityPinBinding.onKeyPressed(key: KeyboardItem) {
        if (key.type == KeyboardItem.TYPE_DELETE_KEY) {
            pinInput.delete()
        } else {
            pinInput.input(key)
        }
    }
}