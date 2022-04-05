package io.outblock.lilico.page.profile.subpage.developer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityDeveloperModeSettingBinding
import io.outblock.lilico.manager.app.chainNetwork
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.profile.subpage.developer.model.DeveloperPageModel
import io.outblock.lilico.page.profile.subpage.developer.presenter.DeveloperModePresenter
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.logd

class DeveloperModeActivity : BaseActivity() {
    private lateinit var binding: ActivityDeveloperModeSettingBinding
    private lateinit var presenter: DeveloperModePresenter
    private lateinit var viewModel: DeveloperModeViewModel

    private val initNetWork = chainNetwork()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeveloperModeSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.neutrals12).light(!isNightMode(this)).applyStatusBar()
        setupToolbar()

        presenter = DeveloperModePresenter(this, binding)
        viewModel = ViewModelProvider(this)[DeveloperModeViewModel::class.java].apply {
            progressVisibleLiveData.observe(this@DeveloperModeActivity) { presenter.bind(DeveloperPageModel(progressDialogVisible = it)) }
            resultLiveData.observe(this@DeveloperModeActivity) { presenter.bind(DeveloperPageModel(result = it)) }
        }
        logd(TAG, "initNetWork:$initNetWork")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        if (initNetWork != chainNetwork()) {
            MainActivity.relaunch(this)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    companion object {
        private val TAG = DeveloperModeActivity::class.java.simpleName
        fun launch(context: Context) {
            context.startActivity(Intent(context, DeveloperModeActivity::class.java))
        }
    }
}