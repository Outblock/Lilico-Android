package io.outblock.lilico.page.walletcreate.fragments.warning

import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletCreateWarningBinding
import io.outblock.lilico.page.walletcreate.WalletCreateViewModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color

class WalletCreateWarningPresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreateWarningBinding,
) : BasePresenter<WalletCreateWarningModel> {

    private val pageViewModel by lazy { ViewModelProvider(fragment.requireActivity())[WalletCreateViewModel::class.java] }
    private val viewModel by lazy { ViewModelProvider(fragment)[WalletCreateWarningViewModel::class.java] }

    private var isRequesting = false

    init {
        with(binding) {
            warningCheck1.setOnCheckedChangeListener { _, _ -> onCheckChanged() }
            warningCheck2.setOnCheckedChangeListener { _, _ -> onCheckChanged() }
            warningCheck3.setOnCheckedChangeListener { _, _ -> onCheckChanged() }
            nextButton.setOnClickListener {
                if (!isRequesting) {
                    isRequesting = true
                    updateButtonState()
                    viewModel.register()
                }
            }
            title1.text = SpannableString(R.string.things_you.res2String()).apply {
                val protection = R.string.know.res2String()
                val index = indexOf(protection)
                setSpan(ForegroundColorSpan(R.color.colorSecondary.res2color()), index, index + protection.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    override fun bind(model: WalletCreateWarningModel) {
        model.isRegisterSuccess?.let { registerCallback(it) }
    }

    private fun registerCallback(isRegisterSuccess: Boolean) {
        isRequesting = false
        if (isRegisterSuccess) {
            pageViewModel.nextStep()
        } else {
            updateButtonState()
            Toast.makeText(fragment.requireContext(), R.string.register_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonState() {
        with(binding.nextButton) {
            setProgressVisible(isRequesting)
            if (isRequesting) {
                setText(R.string.almost_there)
            } else {
                setText(R.string.confirm)
            }
        }
    }


    private fun onCheckChanged() {
        val uncheckedColor = R.color.border_3.res2color()
        val checkedColor = R.color.colorSecondary.res2color()
        with(binding) {
            val isConfirmed = warningCheck1.isChecked && warningCheck2.isChecked && warningCheck3.isChecked
            nextButton.isEnabled = isConfirmed
            warning1.backgroundTintList = ColorStateList.valueOf(if (warningCheck1.isChecked) checkedColor else uncheckedColor)
            warning2.backgroundTintList = ColorStateList.valueOf(if (warningCheck2.isChecked) checkedColor else uncheckedColor)
            warning3.backgroundTintList = ColorStateList.valueOf(if (warningCheck3.isChecked) checkedColor else uncheckedColor)
        }
    }
}