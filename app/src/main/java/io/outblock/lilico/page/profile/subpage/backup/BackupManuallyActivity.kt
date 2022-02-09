package io.outblock.lilico.page.profile.subpage.backup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityBackupManuallyBinding

class BackupManuallyActivity : BaseActivity() {

    private lateinit var binding: ActivityBackupManuallyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupManuallyBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, BackupManuallyActivity::class.java))
        }
    }
}