package io.outblock.lilico.page.profile.subpage.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.databinding.ActivityAccountSettingBinding
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.page.profile.subpage.avatar.ViewAvatarActivity
import io.outblock.lilico.page.profile.subpage.nickname.EditNicknameActivity
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.uiScope

class AccountSettingActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.neutrals12).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()
        setupToolbar()
        setup(intent.getParcelableExtra<UserInfoData>(EXTRA_USER_INFO)!!)
    }

    override fun onRestart() {
        super.onRestart()
        ioScope {
            userInfoCache().read()?.let { uiScope { setup(it) } }
        }
    }

    private fun setup(userInfo: UserInfoData) {
        with(binding) {
            avatarPreference.setImageUrl(userInfo.avatar)
            nicknamePreference.setDesc(userInfo.nickname)
            visiblePreference.updateState(true)

            avatarPreference.setOnClickListener { ViewAvatarActivity.launch(this@AccountSettingActivity, userInfo) }
            nicknamePreference.setOnClickListener { EditNicknameActivity.launch(this@AccountSettingActivity) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
//        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = ""
    }

    companion object {
        private const val EXTRA_USER_INFO = "extra_user_info"

        fun launch(context: Context, userInfo: UserInfoData) {
            context.startActivity(Intent(context, AccountSettingActivity::class.java).apply {
                putExtra(EXTRA_USER_INFO, userInfo)
            })
        }
    }
}