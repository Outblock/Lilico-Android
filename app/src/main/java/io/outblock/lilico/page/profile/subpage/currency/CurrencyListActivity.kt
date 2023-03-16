package io.outblock.lilico.page.profile.subpage.currency

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySettingsCurrencyBinding
import io.outblock.lilico.databinding.ActivitySettingsThemeBinding
import io.outblock.lilico.page.profile.subpage.currency.model.CurrencyModel
import io.outblock.lilico.page.profile.subpage.currency.presenter.CurrencyPresenter
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.getThemeMode
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.updateThemeMode

class CurrencyListActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsCurrencyBinding
    private lateinit var presenter: CurrencyPresenter

    private lateinit var viewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()
        UltimateBarX.with(this).fitWindow(true).light(!isNightMode(this)).applyNavigationBar()

        setupToolbar()

        presenter = CurrencyPresenter(this, binding)

        viewModel = ViewModelProvider(this)[CurrencyViewModel::class.java].apply {
            dataLiveData.observe(this@CurrencyListActivity) { presenter.bind(CurrencyModel(data = it)) }
            load()
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
        title = R.string.currency.res2String()
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, CurrencyListActivity::class.java))
        }
    }
}