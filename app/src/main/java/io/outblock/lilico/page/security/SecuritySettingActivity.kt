package io.outblock.lilico.page.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySecuritySettingBinding
import io.outblock.lilico.manager.biometric.BlockBiometricManager
import io.outblock.lilico.page.security.pin.SecurityPinActivity
import io.outblock.lilico.page.security.recovery.SecurityRecoveryActivity
import io.outblock.lilico.page.security.recovery.SecurityRecoveryActivity.Companion.TYPE_PHRASES
import io.outblock.lilico.page.security.recovery.SecurityRecoveryActivity.Companion.TYPE_PRIVATE_KEY
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.isBiometricEnable
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.setBiometricEnable
import io.outblock.lilico.utils.uiScope

class SecuritySettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySecuritySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecuritySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.neutrals12).light(!isNightMode(this)).applyStatusBar()
        binding.root.addStatusBarTopPadding()
        setupToolbar()
        setup()
    }

    private fun setup() {
        with(binding) {
            pinPreference.setOnClickListener { SecurityPinActivity.launch(this@SecuritySettingActivity, SecurityPinActivity.TYPE_RESET) }
            biometricsPreference.setOnCheckedChangeListener {
                if (it) {
                    BlockBiometricManager.showBiometricPrompt(this@SecuritySettingActivity) { isSuccess ->
                        uiScope { biometricsPreference.setChecked(isSuccess) }
                        if (isSuccess) {
                            setBiometricEnable(true)
                        } else {
                            setBiometricEnable(false)
                            Toast.makeText(this@SecuritySettingActivity, "Auth error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            privatePreference.setOnClickListener { SecurityRecoveryActivity.launch(this@SecuritySettingActivity, TYPE_PRIVATE_KEY) }
            recoveryPreference.setOnClickListener { SecurityRecoveryActivity.launch(this@SecuritySettingActivity, TYPE_PHRASES) }

            uiScope { biometricsPreference.setChecked(isBiometricEnable()) }
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.security.res2String()
    }

    companion object {
        private const val EXTRA_USER_INFO = "extra_user_info"

        fun launch(context: Context) {
            context.startActivity(Intent(context, SecuritySettingActivity::class.java))
        }
    }
}