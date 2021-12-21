package io.outblock.lilico.page.walletcreate.fragments.cloudpwd

import android.content.res.ColorStateList
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletCreateCloudPwdBinding
import io.outblock.lilico.page.walletcreate.WALLET_CREATE_STEP_PIN_CODE
import io.outblock.lilico.page.walletcreate.WalletCreateViewModel
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.listeners.SimpleTextWatcher
import io.outblock.lilico.utils.verifyPassword

class WalletCreateCloudPwdPresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreateCloudPwdBinding,
) : BasePresenter<WalletCreateCloudPwdModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[WalletCreateCloudPwdViewModel::class.java] }
    private val pageViewModel by lazy { ViewModelProvider(fragment.requireActivity())[WalletCreateViewModel::class.java] }

    init {
        with(binding) {
            pwdText1.setOnFocusChangeListener { view, hasFocus -> onFocusChange(view, hasFocus) }
            pwdText2.setOnFocusChangeListener { view, hasFocus -> onFocusChange(view, hasFocus) }

            pwdText1.addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    checkPassword(s.toString())
                }
            })

            pwdText2.addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateButtonState()
                }
            })
            checkBox.setOnCheckedChangeListener { _, _ -> updateButtonState() }

            nextButton.setOnClickListener { uploadMnemonic() }
        }
    }

    override fun bind(model: WalletCreateCloudPwdModel) {
        model.isBackupSuccess?.let { onBackupCallback(it) }
    }

    private fun onBackupCallback(isSuccess: Boolean) {
        if (isSuccess) {
            pageViewModel.changeStep(WALLET_CREATE_STEP_PIN_CODE)
        } else {
            updateContentViewState(true)
            binding.nextButton.setProgressVisible(false)
            binding.nextButton.setText(R.string.next)
            Toast.makeText(fragment.requireContext(), R.string.backup_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonState() {
        with(binding) {
            val pwd1 = pwdText1.text.toString()
            val pwd2 = pwdText2.text.toString()
            val isEnable = !pwdTextTips1.isVisible() && pwd1.isNotEmpty() && pwd1 == pwd2 && checkBox.isChecked
            nextButton.isEnabled = isEnable
            if (isEnable) {
                nextButton.setText(R.string.next)
            }
        }
    }

    private fun checkPassword(pwd: String) {
        val verifyStr = verifyPassword(pwd)
        if (verifyStr.isNullOrEmpty()) {
            binding.pwdTextTips1.setVisible(false)
        } else {
            binding.pwdTextTips1.setVisible(true)
            binding.stateText.text = verifyStr
        }
    }

    private fun onFocusChange(view: View, hasFocus: Boolean) {
        view.backgroundTintList =
            if (hasFocus) ColorStateList.valueOf(R.color.text.res2color()) else ColorStateList.valueOf(R.color.border_3.res2color())
    }

    private fun uploadMnemonic() {
        with(binding) {
            nextButton.setProgressVisible(true)
            nextButton.setText(R.string.backing_up)
            viewModel.backup(fragment.requireContext(), pwdText1.text.toString())
            updateContentViewState(false)
        }
    }

    private fun updateContentViewState(isEnable: Boolean) {
        with(binding) {
            pwdText1.isEnabled = isEnable
            pwdText2.isEnabled = isEnable
            checkBox.isEnabled = isEnable
        }
    }

}