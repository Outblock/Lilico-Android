package io.outblock.lilico.page.walletrestore.presenter

import androidx.transition.Fade
import androidx.transition.Transition
import com.google.android.material.transition.MaterialSharedAxis
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityRestoreWalletBinding
import io.outblock.lilico.manager.drive.DriveItem
import io.outblock.lilico.page.walletcreate.getRootIdByStep
import io.outblock.lilico.page.walletrestore.*
import io.outblock.lilico.page.walletrestore.fragments.drivepwd.WalletRestoreDrivePasswordFragment
import io.outblock.lilico.page.walletrestore.fragments.driveusername.WalletRestoreDriveUsernameFragment
import io.outblock.lilico.page.walletrestore.fragments.guide.WalletRestoreGuideFragment
import io.outblock.lilico.page.walletrestore.fragments.mnemonic.WalletRestoreMnemonicFragment
import io.outblock.lilico.page.walletrestore.model.WalletRestoreContentModel


class WalletRestoreContentPresenter(
    private val activity: WalletRestoreActivity,
    private val binding: ActivityRestoreWalletBinding,
) : BasePresenter<WalletRestoreContentModel> {

    private var currentStep = -1

    override fun bind(model: WalletRestoreContentModel) {
        model.changeStep?.let { onStepChane(it.first, it.second) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onStepChane(step: Int, arguments: Any?) {
        val transition = createTransition(currentStep, step)
        val fragment = when (step) {
            WALLET_RESTORE_STEP_GUIDE -> WalletRestoreGuideFragment()
            WALLET_RESTORE_STEP_DRIVE_USERNAME -> WalletRestoreDriveUsernameFragment.instance(arguments as? ArrayList<DriveItem>)
            WALLET_RESTORE_STEP_DRIVE_PASSWORD -> {
                val data = if (arguments is DriveItem) arguments else (arguments as? List<DriveItem>)?.firstOrNull()
                WalletRestoreDrivePasswordFragment.instance(data)
            }
            WALLET_RESTORE_STEP_MNEMONIC -> WalletRestoreMnemonicFragment()
            else -> WalletRestoreGuideFragment()
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