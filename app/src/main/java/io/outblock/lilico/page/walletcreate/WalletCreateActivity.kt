package io.outblock.lilico.page.walletcreate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityCreateWalletBinding
import io.outblock.lilico.page.walletcreate.model.WalletCreateContentModel
import io.outblock.lilico.page.walletcreate.presenter.WalletCreateContentPresenter

class WalletCreateActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateWalletBinding
    private lateinit var contentPresenter: WalletCreateContentPresenter
    private lateinit var viewModel: WalletCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentPresenter = WalletCreateContentPresenter(this, binding)

        viewModel = ViewModelProvider(this)[WalletCreateViewModel::class.java].apply {
            onStepChangeLiveData.observe(this@WalletCreateActivity, { contentPresenter.bind(WalletCreateContentModel(changeStep = it)) })
        }

        setupToolbar()
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
        title = ""
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, WalletCreateActivity::class.java))
        }
    }
}