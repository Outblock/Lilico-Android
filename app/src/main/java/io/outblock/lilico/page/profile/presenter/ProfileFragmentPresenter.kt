package io.outblock.lilico.page.profile.presenter

import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentProfileBinding
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.profile.ProfileFragment
import io.outblock.lilico.page.profile.model.ProfileFragmentModel
import io.outblock.lilico.page.profile.subpage.accountsetting.AccountSettingActivity
import io.outblock.lilico.page.profile.subpage.backup.BackupSettingActivity
import io.outblock.lilico.page.security.SecuritySettingActivity
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isBackupGoogleDrive
import io.outblock.lilico.utils.uiScope

class ProfileFragmentPresenter(
    private val fragment: ProfileFragment,
    private val binding: FragmentProfileBinding,
) : BasePresenter<ProfileFragmentModel> {

    private var userInfo: UserInfoData? = null

    init {
        binding.root.addStatusBarTopPadding()
        binding.editButton.setOnClickListener {
            userInfo?.let { AccountSettingActivity.launch(fragment.requireContext(), it) }
        }

        binding.group1.backupPreference.setOnClickListener { BackupSettingActivity.launch(fragment.requireContext()) }
        binding.group1.securityPreference.setOnClickListener { SecuritySettingActivity.launch(fragment.requireContext()) }
        updatePreferenceState()
    }

    override fun bind(model: ProfileFragmentModel) {
        model.userInfo?.let { bindUserInfo(it) }
        model.onResume?.let { updatePreferenceState() }
    }

    private fun bindUserInfo(userInfo: UserInfoData) {
        this.userInfo = userInfo
        with(binding) {
            val loader = ImageLoader.Builder(avatarView.context).componentRegistry {
                add(SvgDecoder(avatarView.context))
            }.build()
            avatarView.load(userInfo.avatar, loader) { placeholder(R.drawable.placeholder) }
            useridView.text = userInfo.username
            nicknameView.text = userInfo.nickname
        }
    }

    private fun updatePreferenceState() {
        ioScope {
            val isBackupGoogleDrive = isBackupGoogleDrive()
            uiScope {
                with(binding.group1.backupPreference) {
                    setStateVisible(isBackupGoogleDrive)
                    setDesc(R.string.manually.res2String())
                }
            }
        }
    }
}