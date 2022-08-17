package io.outblock.lilico.page.profile.subpage.walletconnect.session

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityWalletConnectSessionBinding
import io.outblock.lilico.page.profile.subpage.walletconnect.session.adapter.WalletConnectSessionsAdapter
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.isNightMode
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class WalletConnectSessionActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletConnectSessionBinding
    private lateinit var viewModel: WalletConnectSessionViewModel

    private val adapter = WalletConnectSessionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletConnectSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()

        viewModel = ViewModelProvider(this)[WalletConnectSessionViewModel::class.java].apply {
            dataListLiveData.observe(this@WalletConnectSessionActivity) { adapter.setNewDiffData(it) }
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@WalletConnectSessionActivity)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 8.dp2px().toInt()))
            adapter = this@WalletConnectSessionActivity.adapter
        }
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
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
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, WalletConnectSessionActivity::class.java))
        }
    }
}