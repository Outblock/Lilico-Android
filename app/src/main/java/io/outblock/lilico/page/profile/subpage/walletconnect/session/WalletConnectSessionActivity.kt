package io.outblock.lilico.page.profile.subpage.walletconnect.session

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanOptions
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityWalletConnectSessionBinding
import io.outblock.lilico.page.profile.subpage.walletconnect.session.adapter.WalletConnectSessionsAdapter
import io.outblock.lilico.page.scan.dispatchScanResult
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.utils.launch
import io.outblock.lilico.utils.registerBarcodeLauncher
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration
import io.outblock.lilico.widgets.setColorTint

class WalletConnectSessionActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletConnectSessionBinding
    private lateinit var viewModel: WalletConnectSessionViewModel

    private val adapter = WalletConnectSessionsAdapter()

    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeLauncher = registerBarcodeLauncher { result -> dispatchScanResult(this, result.orEmpty()) }

        binding = ActivityWalletConnectSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()

        viewModel = ViewModelProvider(this)[WalletConnectSessionViewModel::class.java].apply {
            dataListLiveData.observe(this@WalletConnectSessionActivity) {
                binding.emptyWrapper.setVisible(it.isEmpty())
                adapter.setNewDiffData(it)
            }
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@WalletConnectSessionActivity)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 8.dp2px().toInt()))
            adapter = this@WalletConnectSessionActivity.adapter
        }

        binding.scanLottieView.setColorTint(R.color.wallet_connect.res2color())
        binding.connectButton.setOnClickListener { barcodeLauncher.launch() }
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.qr_scan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_scan -> barcodeLauncher.launch()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, WalletConnectSessionActivity::class.java))
        }
    }
}