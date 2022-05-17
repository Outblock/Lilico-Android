package io.outblock.lilico.page.profile.presenter

import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentProfileBinding
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.address.AddressBookActivity
import io.outblock.lilico.page.main.HomeTab
import io.outblock.lilico.page.main.MainActivityViewModel
import io.outblock.lilico.page.profile.ProfileFragment
import io.outblock.lilico.page.profile.model.ProfileFragmentModel
import io.outblock.lilico.page.profile.subpage.accountsetting.AccountSettingActivity
import io.outblock.lilico.page.profile.subpage.avatar.ViewAvatarActivity
import io.outblock.lilico.page.profile.subpage.backup.BackupSettingActivity
import io.outblock.lilico.page.profile.subpage.developer.DeveloperModeActivity
import io.outblock.lilico.page.profile.subpage.theme.ThemeSettingActivity
import io.outblock.lilico.page.security.SecuritySettingActivity
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.setVisible

class ProfileFragmentPresenter(
    private val fragment: ProfileFragment,
    private val binding: FragmentProfileBinding,
) : BasePresenter<ProfileFragmentModel> {

    private val context = fragment.requireContext()
    private var userInfo: UserInfoData? = null

    init {
        binding.root.addStatusBarTopPadding()
        binding.userInfo.editButton.setOnClickListener {
            userInfo?.let { AccountSettingActivity.launch(fragment.requireContext(), it) }
        }
        binding.notLoggedIn.root.setOnClickListener {
            ViewModelProvider(fragment.requireActivity())[MainActivityViewModel::class.java].changeTab(HomeTab.WALLET)
        }
        binding.actionGroup.addressButton.setOnClickListener { AddressBookActivity.launch(context) }
        binding.group1.backupPreference.setOnClickListener { BackupSettingActivity.launch(context) }
        binding.group1.securityPreference.setOnClickListener { SecuritySettingActivity.launch(context) }
        binding.group1.developerModePreference.setOnClickListener { DeveloperModeActivity.launch(context) }
        binding.group2.themePreference.setOnClickListener { ThemeSettingActivity.launch(context) }
        updatePreferenceState()
    }

    override fun bind(model: ProfileFragmentModel) {
        model.userInfo?.let { bindUserInfo(it) }
        model.onResume?.let { updatePreferenceState() }
    }

    private fun bindUserInfo(userInfo: UserInfoData) {
        this.userInfo = userInfo
        with(binding.userInfo) {
            avatarView.loadAvatar(userInfo.avatar)
            useridView.text = userInfo.username
            nicknameView.text = userInfo.nickname

            avatarView.setOnClickListener { ViewAvatarActivity.launch(context, userInfo) }
        }
    }

    private fun updatePreferenceState() {
        ioScope {
            val isBackupGoogleDrive = isBackupGoogleDrive()
            val isSignIn = isRegistered()
            uiScope {
                with(binding.group1.backupPreference) {
                    setStateVisible(isBackupGoogleDrive)
                    setDesc(R.string.manually.res2String())
                }
                with(binding) {
                    userInfo.root.setVisible(isSignIn)
                    notLoggedIn.root.setVisible(!isSignIn)
                    actionGroup.root.setVisible(isSignIn)
                    group1.root.setVisible(isSignIn)
                    group2.themePreference.setDesc(if (isNightMode(fragment.requireActivity())) R.string.dark.res2String() else R.string.light.res2String())
                }
            }
        }
    }
}