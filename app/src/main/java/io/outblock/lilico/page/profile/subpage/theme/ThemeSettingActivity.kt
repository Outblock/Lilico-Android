package io.outblock.lilico.page.profile.subpage.theme

import android.content.Context
import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySettingsThemeBinding
import io.outblock.lilico.page.profile.subpage.currency.CurrencyListActivity
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.getThemeMode
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.updateThemeMode

class ThemeSettingActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsThemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()

        setupToolbar()

        with(binding) {
            lightGroup.setOnClickListener { updateTheme(AppCompatDelegate.MODE_NIGHT_NO) }
            darkGroup.setOnClickListener { updateTheme(AppCompatDelegate.MODE_NIGHT_YES) }
            autoPreference.setOnCheckedChangeListener { isAuto -> updateTheme(if (isAuto) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_NO) }
        }

        uiScope { updateUi(getThemeMode()) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun updateTheme(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        updateThemeMode(themeMode)
        updateUi(themeMode)
    }

    private fun updateUi(themeMode: Int) {
        with(binding) {
            lightCheckBox.setImageResource(if (themeMode == AppCompatDelegate.MODE_NIGHT_NO) R.drawable.ic_check_round else R.drawable.ic_check_normal)
            darkCheckBox.setImageResource(if (themeMode == AppCompatDelegate.MODE_NIGHT_YES) R.drawable.ic_check_round else R.drawable.ic_check_normal)
            val isAuto = themeMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            autoPreference.setChecked(isAuto)
            lightGroup.alpha = if (isAuto) 0.5f else 1.0f
            darkGroup.alpha = if (isAuto) 0.5f else 1.0f
            lightGroup.isEnabled = !isAuto
            darkGroup.isEnabled = !isAuto
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.theme.res2String()
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, ThemeSettingActivity::class.java))
        }
    }
}