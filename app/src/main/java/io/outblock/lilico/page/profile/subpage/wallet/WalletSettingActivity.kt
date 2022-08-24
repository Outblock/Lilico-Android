package io.outblock.lilico.page.profile.subpage.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityWalletSettingBinding
import io.outblock.lilico.page.profile.subpage.claimdomain.ClaimDomainActivity
import io.outblock.lilico.page.profile.subpage.claimdomain.checkMeowDomainClaimed
import io.outblock.lilico.page.profile.subpage.wallet.dialog.WalletResetConfirmDialog
import io.outblock.lilico.page.security.recovery.SecurityPrivateKeyActivity
import io.outblock.lilico.page.security.recovery.SecurityRecoveryActivity
import io.outblock.lilico.page.security.securityOpen
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.setVisible

class WalletSettingActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()
        setupToolbar()
        setup()
        checkMeowDomainClaimed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setup() {
        with(binding) {
            privatePreference.setOnClickListener {
                securityOpen(SecurityPrivateKeyActivity.launchIntent(this@WalletSettingActivity))
            }
            recoveryPreference.setOnClickListener {
                securityOpen(SecurityRecoveryActivity.launchIntent(this@WalletSettingActivity))
            }

            uiScope { freeGasPreference.setChecked(isFreeGasPreferenceEnable()) }
            freeGasPreference.setOnCheckedChangeListener { uiScope { setFreeGasPreferenceEnable(it) } }

            resetButton.setOnClickListener { WalletResetConfirmDialog.show(supportFragmentManager) }

            claimButton.setOnClickListener { ClaimDomainActivity.launch(this@WalletSettingActivity) }

            uiScope { claimDomainWrapper.setVisible(!isMeowDomainClaimed()) }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.wallet.res2String()
    }

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, WalletSettingActivity::class.java))
        }
    }
}