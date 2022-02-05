package io.outblock.lilico.page.profile.subpage.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityAccountSettingBinding
import io.outblock.lilico.network.model.UserInfoData
import io.outblock.lilico.utils.isNightMode

class AccountSettingActivity : BaseActivity() {

    private val userInfo by lazy { intent.getParcelableExtra<UserInfoData>(EXTRA_USER_INFO)!! }

    private lateinit var binding: ActivityAccountSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.neutrals12).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()
        setupToolbar()
        setup()
    }

    private fun setup() {
        with(binding) {
            avatarPreference.setImageUrl(userInfo.avatar)
            nicknamePreference.setDesc(userInfo.nickname)
            visiblePreference.updateState(true)
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