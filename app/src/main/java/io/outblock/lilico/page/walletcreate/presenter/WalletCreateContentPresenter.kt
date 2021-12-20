package io.outblock.lilico.page.walletcreate.presenter

import androidx.transition.Fade
import androidx.transition.Transition
import com.google.android.material.transition.MaterialSharedAxis
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityCreateWalletBinding
import io.outblock.lilico.page.walletcreate.*
import io.outblock.lilico.page.walletcreate.fragments.cloudpwd.WalletCreateCloudPwdFragment
import io.outblock.lilico.page.walletcreate.fragments.mnemonic.WalletCreateMnemonicFragment
import io.outblock.lilico.page.walletcreate.fragments.mnemoniccheck.WalletCreateMnemonicCheckFragment
import io.outblock.lilico.page.walletcreate.fragments.pincode.WalletCreatePinCodeFragment
import io.outblock.lilico.page.walletcreate.fragments.username.WalletCreateUsernameFragment
import io.outblock.lilico.page.walletcreate.fragments.warning.WalletCreateWarningFragment
import io.outblock.lilico.page.walletcreate.model.WalletCreateContentModel


class WalletCreateContentPresenter(
    private val activity: WalletCreateActivity,
    private val binding: ActivityCreateWalletBinding,
) : BasePresenter<WalletCreateContentModel> {

    private var currentStep = -1

    override fun bind(model: WalletCreateContentModel) {
        model.changeStep?.let { onStepChane(it) }
    }

    private fun onStepChane(step: Int) {
        val transition = createTransition(currentStep, step)
        val fragment = when (step) {
            WALLET_CREATE_STEP_WARNING -> WalletCreateWarningFragment()
            WALLET_CREATE_STEP_MNEMONIC -> WalletCreateMnemonicFragment()
            WALLET_CREATE_STEP_CLOUD_PWD -> WalletCreateCloudPwdFragment()
            WALLET_CREATE_STEP_MNEMONIC_CHECK -> WalletCreateMnemonicCheckFragment()
            WALLET_CREATE_STEP_PIN_CODE -> WalletCreatePinCodeFragment()
            else -> WalletCreateUsernameFragment()
        }
        fragment.enterTransition = transition
        activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        currentStep = step
    }

    private fun createTransition(currentStep: Int, newStep: Int): Transition {
        if (currentStep < 0) {
            return Fade().apply { duration = 50 }
        }
        val transition = MaterialSharedAxis(MaterialSharedAxis.X, currentStep < newStep)

        transition.addTarget(getRootIdByStep(currentStep))
        transition.addTarget(getRootIdByStep(newStep))
        return transition
    }
}