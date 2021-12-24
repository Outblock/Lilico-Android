package io.outblock.lilico.page.walletcreate.fragments.pincode

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.FragmentWalletCreatePinCodeBinding
import io.outblock.lilico.manager.biometric.BlockBiometricManager
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible

class WalletCreatePinCodePresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreatePinCodeBinding,
) {

    init {
        with(binding) {
            title1.text = SpannableString(R.string.pin_code_title.res2String()).apply {
                val protection = R.string.protection.res2String()
                val index = indexOf(protection)
                setSpan(ForegroundColorSpan(R.color.colorSecondary.res2color()), index, index + protection.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            faceIdLayout.setOnClickListener {
                BlockBiometricManager.showBiometricPrompt(fragment.requireActivity()) { isSuccess ->
                    if (isSuccess) {
                        MainActivity.launch(fragment.requireContext())
                    } else {
                        Toast.makeText(fragment.requireContext(), "Auth error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            pinCodeLayout.setOnClickListener { }

            faceIdLayout.setVisible(BlockBiometricManager.checkIsBiometricEnable(fragment.requireContext()))
        }
    }
}