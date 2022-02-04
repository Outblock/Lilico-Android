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

class ProfileFragmentPresenter(
    private val fragment: ProfileFragment,
    private val binding: FragmentProfileBinding,
) : BasePresenter<ProfileFragmentModel> {

    init {
        binding.root.addStatusBarTopPadding()
    }

    override fun bind(model: ProfileFragmentModel) {
        model.userInfo?.let { bindUserInfo(it) }
    }

    private fun bindUserInfo(userInfo: UserInfoData) {
        with(binding) {
            val loader = ImageLoader.Builder(avatarView.context).componentRegistry {
                add(SvgDecoder(avatarView.context))
            }.build()
            avatarView.load(userInfo.avatar, loader) { placeholder(R.drawable.placeholder) }
            useridView.text = userInfo.username
            nicknameView.text = userInfo.nickname
        }
    }
}