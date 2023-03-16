package io.outblock.lilico.page.profile.subpage.walletconnect.sessiondetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityWalletConnectSessionDetailBinding
import io.outblock.lilico.manager.walletconnect.WalletConnect
import io.outblock.lilico.page.profile.subpage.walletconnect.sessiondetail.model.WalletConnectSessionDetailModel
import io.outblock.lilico.utils.extensions.capitalizeV2
import io.outblock.lilico.utils.extensions.urlHost
import io.outblock.lilico.utils.formatDate
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isNightMode

class WalletConnectSessionDetailActivity : BaseActivity() {

    private val data by lazy { intent.getParcelableExtra<WalletConnectSessionDetailModel>(EXTRA_DATA)!! }
    private lateinit var binding: ActivityWalletConnectSessionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletConnectSessionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(true).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()
        setupToolbar()

        with(binding) {
            Glide.with(iconView).load(data.icon).into(iconView)
            titleView.text = data.name
            descView.text = data.url.urlHost()
            addressView.text = "(${data.address})"
            timeView.text = (data.expiry * 1000).formatDate("HH:mma, MMM dd")
            networkView.text = data.network.capitalizeV2()
        }

        binding.disconnectButton.setOnClickListener {
            ioScope {
                WalletConnect.get().disconnect(data.topic)
                finish()
            }
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
    }

    companion object {
        private const val EXTRA_DATA = "EXTRA_DATA"

        fun launch(context: Context, data: WalletConnectSessionDetailModel) {
            context.startActivity(Intent(context, WalletConnectSessionDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, data)
            })
        }
    }
}