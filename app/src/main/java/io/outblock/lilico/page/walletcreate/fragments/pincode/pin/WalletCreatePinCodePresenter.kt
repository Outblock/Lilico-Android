package io.outblock.lilico.page.walletcreate.fragments.pincode.pin

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.FragmentWalletCreatePinCodeBinding
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.KeyboardType
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets.keyboardT9Normal
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.savePinCode

class WalletCreatePinCodePresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreatePinCodeBinding,
) {

    init {
        with(binding) {
            title1.text = SpannableString(R.string.create_pin.res2String()).apply {
                val protection = R.string.pin.res2String()
                val index = indexOf(protection)
                setSpan(ForegroundColorSpan(R.color.colorSecondary.res2color()), index, index + protection.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            pinInput.setCheckCallback { passed ->
                savePinCode(pinInput.keys().joinToString("") { "${it.number}" })
                if (passed) MainActivity.launch(fragment.requireContext())
            }
            pinKeyboard.setOnKeyboardActionListener { onKeyPressed(it) }
            pinKeyboard.bindKeys(keyboardT9Normal, KeyboardType.T9)
            pinKeyboard.show()
        }
    }

    private fun FragmentWalletCreatePinCodeBinding.onKeyPressed(key: KeyboardItem) {
        if (key.type == KeyboardItem.TYPE_DELETE_KEY) {
            pinInput.delete()
        } else {
            pinInput.input(key)
            if (pinInput.keys().size == 6 && !pinInput.isChecking()) {
                pinInput.checking()
                checkMode()
            }
        }
    }

    private fun checkMode() {
        with(binding) {
            title1.text = SpannableString(R.string.check_pin.res2String()).apply {
                val protection = R.string.pin.res2String()
                val index = indexOf(protection)
                setSpan(ForegroundColorSpan(R.color.colorSecondary.res2color()), index, index + protection.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            title2.setText(R.string.check_pin_tip)
        }
    }
}